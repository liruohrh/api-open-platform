import { LoginOutlined, LogoutOutlined, SettingOutlined, UserOutlined } from '@ant-design/icons';
import { history, useModel } from '@umijs/max';
import { createStyles } from 'antd-style';
import { stringify } from 'querystring';
import type { MenuInfo } from 'rc-menu/lib/interface';
import React, { useCallback } from 'react';
import { flushSync } from 'react-dom';
import HeaderDropdown from '../HeaderDropdown';
import { logout } from '@/services/api-platform/userController';
import { loginPath } from '@/constants';
import { notification, Spin } from 'antd';

export type GlobalHeaderRightProps = {
  menu?: boolean;
  children?: React.ReactNode;
};

export const AvatarName = () => {
  const { initialState } = useModel('@@initialState');
  const { currentUser } = initialState || {};
  if(currentUser){
    return <span className="anticon">{currentUser.username}</span>;
  }else{
    return <span className="anticon"><UserOutlined/>游客</span>;
  }
};

const useStyles = createStyles(({ token }) => {
  return {
    action: {
      display: 'flex',
      height: '48px',
      marginLeft: 'auto',
      overflow: 'hidden',
      alignItems: 'center',
      padding: '0 8px',
      cursor: 'pointer',
      borderRadius: token.borderRadius,
      '&:hover': {
        backgroundColor: token.colorBgTextHover,
      },
    },
  };
});

export const AvatarDropdown: React.FC<GlobalHeaderRightProps> = ({ menu, children }) => {
  /**
   * 退出登录，并且将当前的 url 保存
   */
  const loginOut = async () => {
    await logout();
    const { search, pathname } = window.location;
    history.replace({
      pathname: loginPath,
      search: stringify({
        redirect: pathname + search,
      }),
    });
  };
  const { styles } = useStyles();

  const { initialState, setInitialState } = useModel('@@initialState');

  const onMenuClick = useCallback(
    async (event: MenuInfo) => {
      const { key } = event;
      if (key === 'logout') {
        await loginOut();
        flushSync(() => {
          setInitialState((s) => ({ ...s, currentUser: undefined }));
        });
      } else if (key === 'login') {
        history.push(loginPath);
      }
      else if (key === 'accountCenter') {
        history.push("/account/center");
      } else{
        notification.error({message: "未知选项"})
      }
    },
    [setInitialState],
  );

  const loading = (
    <span className={styles.action}>
      <Spin
        size="small"
        style={{
          marginLeft: 8,
          marginRight: 8,
        }}
      />
    </span>
  );

  if (!initialState) {
    return loading;
  }

  const { currentUser } = initialState;
  if (!currentUser || !currentUser.username) {
    return (
      <HeaderDropdown
        menu={{
          selectedKeys: [],
          onClick: onMenuClick,
          items: [
            {
              key: 'login',
              icon: <LoginOutlined />,
              label: "登录账号",
            },
          ],
        }}
      >
        {children}
      </HeaderDropdown>
    );
  }


  const menuItems = [
    ...(menu
      ? [
          {
            key: 'accountCenter',
            icon: <UserOutlined />,
            label: '个人中心',
          },
          {
            type: 'divider' as const,
          },
        ]
      : []),
    {
      key: 'logout',
      icon: <LogoutOutlined />,
      label: '退出登录',
    },
  ];

  return (
    <HeaderDropdown
      menu={{
        selectedKeys: [],
        onClick: onMenuClick,
        items: menuItems,
      }}
    >
      {children}
    </HeaderDropdown>
  );
};
