import { resetUserAppSecret } from '@/services/api-platform/userController';
import { useModel } from '@@/exports';
import { ProCard } from '@ant-design/pro-components';
import { Button, notification, Space, Typography } from 'antd';
import React from 'react';

const DeveloperCredentials: React.FC = () => {
  console.log("更新 DeveloperCredentials");
  const { initialState, refresh } = useModel('@@initialState');
  const currentUser = initialState!.currentUser;
  if (!currentUser) {
    return <ProCard loading></ProCard>;
  }
  return (
    <ProCard bordered title="开发者凭证" type="inner">
      <Typography.Paragraph>
        <Space direction={'horizontal'}>
          <Typography.Text>AppKey: </Typography.Text>
          <Typography.Text copyable>{currentUser?.appKey}</Typography.Text>
        </Space>
      </Typography.Paragraph>
      <Space direction={'horizontal'}>
        <Typography.Paragraph>
          <Space direction={'horizontal'}>
            <Typography.Text>AppSecret: </Typography.Text>
            <Typography.Text copyable>{currentUser?.appSecret}</Typography.Text>
          </Space>
        </Typography.Paragraph>
        <Button
          onClick={() => {
            resetUserAppSecret().then(() => {
              refresh();
              notification.success({ message: "重置AppSecret成功" })
            });
          }}
        >
          重置
        </Button>
      </Space>
    </ProCard>
  );
};
export default DeveloperCredentials;
