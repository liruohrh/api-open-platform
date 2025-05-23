
import DebugView from "@/components/DebugView";
import { getApiById } from "@/services/api-platform/apiController";
import { jsonPrettify } from "@/utils";
import { BugOutlined } from "@ant-design/icons";
import { ProCard } from "@ant-design/pro-components";
import { useParams } from "@umijs/max";
import { Space, Typography, Image, Card, Input, Cascader } from "antd";
import { useEffect, useState } from "react";

const APIDetailPage: React.FC = () => {
    const params = useParams();
    const [isLoading, setLoding] = useState(true);
    const [apiInfo, setApiInfo] = useState<API.HttpApiResp>({});
    useEffect(() => {
        let load = true;
        getApiById({ apiId: parseInt(params.id!) })
            .then(resp => {
                if (!load) {
                    return;
                }

                if (resp.data) {
                    const values = resp.data;
                    values.params = jsonPrettify(values?.params, 2);
                    values.reqHeaders = jsonPrettify(values?.reqHeaders, 2);
                    values.reqBody = jsonPrettify(values?.reqBody);
                    values.respHeaders = jsonPrettify(values?.respHeaders, 2);
                    values.respBody = jsonPrettify(values?.respBody);
                    values.respSuccess = jsonPrettify(values?.respSuccess, 2);
                    values.respFail = jsonPrettify(values?.respFail);
                    values.errorCodes = jsonPrettify(values?.errorCodes, 2);
                    setApiInfo(values);
                    setLoding(false);
                }
            })
        return () => {
            load = false;
        }
    }, []);

    const setEmpty = (val: string | undefined) => {
        return val ? val : "空";
    }



    return <>
        <ProCard loading={isLoading}>
            <Space direction={"vertical"} style={{ width: "100%", height: "100%" }}>


                <ProCard>
                    <Space direction={'horizontal'} style={{ width: '100%' }}
                        size={20}
                    >
                        <Space direction={'horizontal'}>
                            <Typography.Text>LOGO：</Typography.Text>
                            <Image src={apiInfo.logoUrl} width={100} height={100} />
                        </Space>
                        <Space direction={"vertical"} style={{ width: '100%', justifyContent: "end" }}>

                            <Typography.Title level={4}>
                                ID：
                                <Typography.Text>
                                    {apiInfo.id}
                                </Typography.Text>
                            </Typography.Title>
                            <Typography.Title level={4}>
                                名称：
                                <Typography.Text>
                                    {apiInfo.name}
                                </Typography.Text>
                            </Typography.Title>
                        </Space>
                    </Space>
                    <Card title="描述" bordered={false}>
                        <Typography.Paragraph>
                            {apiInfo.description}
                        </Typography.Paragraph>
                    </Card>
                    <Card title="价格" bordered={false}>
                        <Typography.Paragraph>
                            {apiInfo?.price === 0 ? "免费" : "💰" + apiInfo?.price + "元"}
                        </Typography.Paragraph>
                    </Card>
                </ProCard>
                <Typography.Title level={3}>
                    文档
                </Typography.Title>
                <ProCard bordered headerBordered gutter={16}>
                    <Input
                        addonBefore={
                            <Cascader
                                disabled
                                placeholder={apiInfo.method}
                                style={{ width: 150 }}
                            />}
                        readOnly
                        defaultValue={`${apiInfo.protocol}://${apiInfo.domain}${apiInfo.path}`}
                    />
                </ProCard>
                <ProCard
                    tabs={{
                        type: 'card',
                    }}
                >
                    <ProCard.TabPane key="req" tab="请求">
                        <ProCard title="请求参数" bordered headerBordered gutter={16}>
                            <pre>
                                {setEmpty(apiInfo.params)}
                            </pre>
                        </ProCard>
                        <ProCard title="请求头" bordered headerBordered gutter={16}>
                            <pre>
                                {setEmpty(apiInfo.reqHeaders)}
                            </pre>
                        </ProCard>
                        <ProCard title="请求体" bordered headerBordered gutter={16}>
                            <pre>
                                {setEmpty(apiInfo.reqBody)}
                            </pre>
                        </ProCard>
                    </ProCard.TabPane>
                    <ProCard.TabPane key="resp" tab="响应">

                        <ProCard title="响应头" bordered headerBordered gutter={16}>
                            <pre>
                                {setEmpty(apiInfo.respHeaders)}
                            </pre>
                        </ProCard>
                        <ProCard title="响应体" bordered headerBordered gutter={16}>
                            <pre>
                                {setEmpty(apiInfo.respBody)}
                            </pre>
                        </ProCard>
                    </ProCard.TabPane>
                    <ProCard.TabPane key="respExample" tab="响应示例">
                        <ProCard title="成功响应示例" bordered headerBordered gutter={16}>
                            <pre>
                                {setEmpty(apiInfo.respSuccess)}
                            </pre>
                        </ProCard>
                        <ProCard title="失败响应示例" bordered headerBordered gutter={16}>
                            <pre>
                                {setEmpty(apiInfo.respFail)}
                            </pre>
                        </ProCard>
                    </ProCard.TabPane>
                    <ProCard.TabPane key="errorCodes" tab="错误码">
                        <ProCard bordered headerBordered gutter={16}>
                            <pre>
                                {setEmpty(apiInfo.errorCodes)}
                            </pre>
                        </ProCard>
                    </ProCard.TabPane>

                    <ProCard.TabPane
                        key="debug"
                        tab={
                            <Typography.Text>
                                <BugOutlined />
                                调试
                            </Typography.Text>
                        }
                    >
                        <DebugView apiId={apiInfo.id!}
                            hasParams={apiInfo.params ? true : false}
                            hasReqHeaders={apiInfo.reqHeaders ? true : false}
                            hasReqBody={apiInfo.reqBody ? true : false}
                        />
                    </ProCard.TabPane>
                </ProCard>
            </Space>
        </ProCard>
    </>
};
export default APIDetailPage;