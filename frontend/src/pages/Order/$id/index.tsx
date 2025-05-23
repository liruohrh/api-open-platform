
import DebugView from "@/components/DebugView";
import { getApiById } from "@/services/api-platform/apiController";
import { getOrderById, optForOrder } from "@/services/api-platform/orderController";
import { formatTimestamp, jsonPrettify } from "@/utils";
import { BugOutlined } from "@ant-design/icons";
import { ProCard } from "@ant-design/pro-components";
import { history, Link, useParams } from "@umijs/max";
import { Space, Typography, Image, Card, Input, Cascader, Badge, Button, notification } from "antd";
import { useEffect, useState } from "react";

const OrderDetailPage: React.FC = () => {
    const params = useParams();
    const [isLoading, setLoding] = useState(true);
    const [orderInfo, setOrderInfo] = useState<API.OrderVo>({});
    useEffect(() => {
        let load = true;
        getOrderById({ orderId: params.id! })
            .then(resp => {
                if (!load) {
                    return;
                }
                if (resp.data) {
                    setOrderInfo(resp.data);
                    setLoding(false);
                }
            })
        return () => {
            load = false;
        }
    }, []);

    return <>
        <ProCard loading={isLoading}>
            <Space direction={"vertical"} style={{ width: "100%", height: "100%" }}>
                <ProCard>
                    <Card bordered={false}>
                        <Typography.Paragraph>Id：{orderInfo.id}</Typography.Paragraph>
                        <Typography.Paragraph>订单编号：{orderInfo.orderId}</Typography.Paragraph>
                        <Typography.Paragraph>apiId：{orderInfo.apiId}</Typography.Paragraph>
                    </Card>
                    <Card bordered={false}>
                        <Typography.Paragraph>
                            实际支付： {orderInfo.actualPayment}
                        </Typography.Paragraph>
                        <Typography.Paragraph>
                            购买数量： {orderInfo.amount}
                        </Typography.Paragraph>
                    </Card>
                    <Card bordered={false}>
                        支付状态： <Badge
                            status={orderInfo.status === 0 ? 'error' : (orderInfo.status === 1 ? 'success' : 'default')}
                            text={orderInfo.status === 0 ? '未支付' : (orderInfo.status === 1 ? '已支付' : '已取消')}
                        />
                    </Card>
                    <Card bordered={false}>
                        <Typography.Paragraph>
                            创建时间： {formatTimestamp(orderInfo.ctime)}
                        </Typography.Paragraph>
                        <Typography.Paragraph>
                            更新时间：  {formatTimestamp(orderInfo.utime)}
                        </Typography.Paragraph>
                    </Card>
                    <Card bordered={false}>
                        <Space direction={"horizontal"} style={{ width: "100%", justifyContent: "center" }}>
                            {
                                (orderInfo.status === 1 || orderInfo.status === 2)
                                    ?
                                    <Button
                                        type={"dashed"}
                                        disabled
                                    >
                                        前往支付
                                    </Button>
                                    : <Link to={`/order/pay/${orderInfo.orderId}`} >
                                        <Button>
                                            前往支付
                                        </Button>
                                    </Link>
                            }

                            <Button
                                type={orderInfo.status === 1 || orderInfo.status === 2 ? "dashed" : "primary"}
                                disabled={orderInfo.status === 1 || orderInfo.status === 2}
                                onClick={async () => {
                                    optForOrder({ orderId: params.id!, isCancel: true })
                                        .then(() => {
                                            notification.success({ message: "取消成功" });
                                            history.push(`/order/list`);

                                        });
                                }}
                            >
                                取消订单
                            </Button>

                        </Space>
                    </Card>
                </ProCard>
            </Space>
        </ProCard>
    </>
};
export default OrderDetailPage;