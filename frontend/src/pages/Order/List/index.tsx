import UpdateForm from '@/pages/Admin/User/components/UpdateForm';
import { listUser, getUserById } from '@/services/api-platform/userController';
import type { ActionType, ProColumns, ProDescriptionsItemProps } from '@ant-design/pro-components';
import { PageContainer, ProDescriptions, ProTable } from '@ant-design/pro-components';
import '@umijs/max';
import { Drawer, notification, Tag, Typography } from 'antd';
import React, { useRef, useState } from 'react';
import { changeSortValue, emptyValueToNull, } from '@/utils';
import { listOrder, optForOrder } from '@/services/api-platform/orderController';
import { history, Link } from '@umijs/max';

const OrderListPage: React.FC = () => {

    const [current, setCurrent] = useState<number>(1);
    const actionRef = useRef<ActionType>();

    /**
     * @en-US International configuration
     * @zh-CN 国际化配置
     * */

    const columns: ProColumns<API.OrderVo>[] = [
        {
            title: 'ID',
            dataIndex: 'id',
            valueType: 'text',
        },
        {
            title: '订单编号',
            dataIndex: 'orderId',
            valueType: 'text',
        },

        {
            title: 'apiId',
            dataIndex: 'apiId',
            valueType: 'text'
        },
        {
            title: '实际支付',
            dataIndex: 'actualPayment',
            valueType: 'text',
            sorter: true
        },
        {
            title: '数量',
            dataIndex: 'amount',
            valueType: 'text',
            sorter: true
        },
        {
            title: '单价',
            dataIndex: 'price',
            valueType: 'text',
            sorter: true
        },
        {
            title: '支付状态',
            dataIndex: 'status',
            valueEnum: {
                0: {
                    text: '待支付',
                    status: 'error',
                },
                1: {
                    text: '已支付',
                    status: 'success',
                },
                2: {
                    text: '已取消',
                    status: 'default',
                },

            },
        },
        {
            title: '创建时间',
            dataIndex: 'ctime',
            valueType: 'text',
            sorter: true
        },
        {
            title: '更新时间',
            dataIndex: 'utime',
            valueType: 'text',
            sorter: true
        },

        {
            title: '操作',
            dataIndex: 'option',
            valueType: 'option',
            render: (_, entity) => (
                <>
                    <Link to={`/order/${entity.orderId}`}
                    >
                        <Tag>
                            查看
                        </Tag>
                    </Link>
                    {entity.status !== 1 && entity.status !== 2 && <Link to={`/order/pay/${entity.orderId}`}>
                        <Tag>
                            支付
                        </Tag>
                    </Link>}

                    {entity.status !== 1 && entity.status !== 2 &&
                        <Tag
                            style={{ cursor: 'pointer' }}
                            onClick={() => {
                                optForOrder({ orderId: entity.orderId!, isCancel: true })
                                    .then(() => {
                                        notification.success({ message: "取消成功" });
                                        actionRef.current?.reload();

                                    });
                            }}
                        >
                            取消
                        </Tag>
                    }
                </>
            ),
        }
    ];

    return (
        <PageContainer>
            <ProTable<API.OrderVo>
                pagination={{
                    // 必须有这2个，虽然最终会根据响应的数组长度重新设置，但是pageSize不可以任意
                    pageSize: 10,
                    current: current
                }}
                headerTitle={'订单表格'}
                actionRef={actionRef}
                rowKey="id"
                cardBordered
                search={{
                    labelWidth: 120,
                }}
                //@ts-ignore
                request={async (params, sort, filter) => {
                    const current = params.current ?? 1;
                    const size = params.pageSize;
                    delete params.current;
                    delete params.pageSize;


                    try {
                        const data: API.RespPageRespUserVo = await listOrder({
                            current,
                            size,
                            sort: changeSortValue(sort),
                            //@ts-ignore
                            // search: emptyValueToNull(params)
                        });
                        setCurrent(current);

                        return {
                            data: data.data?.data,
                            success: true,
                            total: data.data?.total,
                        };
                    } catch (e) {
                        return {
                            success: false,
                        };
                    }
                }}
                columns={columns}
            />
        </PageContainer>
    );
};
export default OrderListPage;
