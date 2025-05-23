
import { ProCard, ProFormField } from "@ant-design/pro-components";
import { Button, Card, Cascader, Image, Input, Modal, Space, Typography } from "antd";
import { jsonPrettify } from "@/utils";
import { BugOutlined } from "@ant-design/icons";
import DebugView from "../../../../components/DebugView";

export type ReadModalProps = {
  handlerCloseModal: () => void;
  isOpenModal: boolean;
  values: API.HttpApiResp
};
const ReadModal: React.FC<ReadModalProps> = ({
  handlerCloseModal,
  isOpenModal,
  values,
}) => {
  if (!values) {
    handlerCloseModal();
    return;
  }

  values.params = jsonPrettify(values?.params, 2);
  values.reqHeaders = jsonPrettify(values?.reqHeaders, 2);
  values.reqBody = jsonPrettify(values?.reqBody);
  values.respHeaders = jsonPrettify(values?.respHeaders, 2);
  values.respBody = jsonPrettify(values?.respBody);
  values.respSuccess = jsonPrettify(values?.respSuccess, 2);
  values.respFail = jsonPrettify(values?.respFail);
  values.errorCodes = jsonPrettify(values?.errorCodes, 2);
  const setEmpty = (val: string | undefined) => {
    return val ? val : "空";
  }




  return (
    <Modal
      title={"查看API详情"}
      open={isOpenModal}
      onCancel={() => { handlerCloseModal() }}
      onClose={() => { handlerCloseModal() }}
      onOk={() => { handlerCloseModal() }}
      destroyOnClose={true}
      width={"80%"}
    >
      <ProCard>

        <Space direction={'horizontal'} style={{ width: '100%' }}
          size={20}
        >
          <Space direction={'horizontal'}>
            <Typography.Text>LOGO：</Typography.Text>
            <Image src={values.logoUrl} width={100} height={100} />
          </Space>
          <Space direction={"vertical"} style={{ width: '100%', justifyContent: "end" }}>

            <Typography.Title level={4}>
              ID：
              <Typography.Text>
                {values.id}
              </Typography.Text>
            </Typography.Title>
            <Typography.Title level={4}>
              名称：
              <Typography.Text>
                {values.name}
              </Typography.Text>
            </Typography.Title>
          </Space>
        </Space>
        <Card title="描述" bordered={false}>
          <Typography.Paragraph>
            {values.description}
          </Typography.Paragraph>
        </Card>


        <Card title="价格" bordered={false}>
          <Typography.Paragraph>
            {values?.price === 0 ? "免费" : "💰" + values?.price + "元"}
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
              placeholder={values.method}
              style={{ width: 150 }}
            />}
          readOnly
          defaultValue={`${values.protocol}://${values.domain}${values.path}`}
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
              {setEmpty(values.params)}
            </pre>
          </ProCard>
          <ProCard title="请求头" bordered headerBordered gutter={16}>
            <pre>
              {setEmpty(values.reqHeaders)}
            </pre>
          </ProCard>
          <ProCard title="请求体" bordered headerBordered gutter={16}>
            <pre>
              {setEmpty(values.reqBody)}
            </pre>
          </ProCard>
        </ProCard.TabPane>
        <ProCard.TabPane key="resp" tab="响应">

          <ProCard title="响应头" bordered headerBordered gutter={16}>
            <pre>
              {setEmpty(values.respHeaders)}
            </pre>
          </ProCard>
          <ProCard title="响应体" bordered headerBordered gutter={16}>
            <pre>
              {setEmpty(values.respBody)}
            </pre>
          </ProCard>
        </ProCard.TabPane>
        <ProCard.TabPane key="respExample" tab="响应示例">
          <ProCard title="成功响应示例" bordered headerBordered gutter={16}>
            <pre>
              {setEmpty(values.respSuccess)}
            </pre>
          </ProCard>
          <ProCard title="失败响应示例" bordered headerBordered gutter={16}>
            <pre>
              {setEmpty(values.respFail)}
            </pre>
          </ProCard>
        </ProCard.TabPane>
        <ProCard.TabPane key="errorCodes" tab="错误码">
          <ProCard bordered headerBordered gutter={16}>
            <pre>
              {setEmpty(values.errorCodes)}
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
          <DebugView apiId={values.id!}
            hasParams={values.params ? true : false}
            hasReqHeaders={values.reqHeaders ? true : false}
            hasReqBody={values.reqBody ? true : false}
          />
        </ProCard.TabPane>
      </ProCard>
    </Modal >
  );
};
export default ReadModal;