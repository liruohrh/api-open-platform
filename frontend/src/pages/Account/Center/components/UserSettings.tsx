import {
  ProCard,
} from '@ant-design/pro-components';
import { Link } from '@umijs/max';
import React from 'react';
import UserInfo from '@/pages/Account/Center/components/UserInfo';
import DeveloperCredentials from '@/pages/Account/Center/components/DeveloperCredentials';

const UserSettings: React.FC = () => {
  console.log("更新 UserSettings");
  return (
    <>
      <ProCard
        title="竖向内部卡片"
        bordered
        headerBordered
        direction="column"
        gutter={[0, 16]}
        style={{ marginBlockStart: 8 }}
      >
        <UserInfo/>
        <ProCard title="其他设置" type="inner" bordered>
          <Link to={'/user/passwd'}>修改密码</Link>
        </ProCard>
        <DeveloperCredentials/>
      </ProCard>
    </>
  );
};
export default UserSettings;
