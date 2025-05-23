import { ProCard } from '@ant-design/pro-components';
import UserSettings from '@/pages/Account/Center/components/UserSettings';
import React from 'react';

const AccountCenter: React.FC = () => {
  console.log("更新 AccountCenter");
  return <>
    <ProCard
      tabs={{
        type: 'card',
      }}
    >
      <ProCard.TabPane key="userSettings" tab="个人设置">
        <UserSettings/>
      </ProCard.TabPane>
      <ProCard.TabPane key="tab2" tab="API">
        内容二
      </ProCard.TabPane>
    </ProCard>
  </>
}
export default AccountCenter;
