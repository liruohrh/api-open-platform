import { Footer } from '@/components';
import {
  AlipayCircleOutlined,
  LockOutlined, MailOutlined,
  TaobaoCircleOutlined,
  UserOutlined,
  WeiboCircleOutlined,
} from '@ant-design/icons';
import {
  LoginForm,
  ProFormCaptcha,
  ProFormText,
} from '@ant-design/pro-components';
import { Helmet, Link, useModel } from '@umijs/max';
import { notification, Tabs } from 'antd';
import { createStyles } from 'antd-style';
import React, { useState } from 'react';
import Settings from '../../../../config/defaultSettings';
import { login } from '@/services/api-platform/userController';
import { getCaptcha } from '@/services/api-platform/emailController';
import { history } from '@@/core/history';


const useStyles = createStyles(({ token }) => {
  return {
    action: {
      marginLeft: '8px',
      color: 'rgba(0, 0, 0, 0.2)',
      fontSize: '24px',
      verticalAlign: 'middle',
      cursor: 'pointer',
      transition: 'color 0.3s',
      '&:hover': {
        color: token.colorPrimaryActive,
      },
    },
    lang: {
      width: 42,
      height: 42,
      lineHeight: '42px',
      position: 'fixed',
      right: 16,
      borderRadius: token.borderRadius,
      ':hover': {
        backgroundColor: token.colorBgTextHover,
      },
    },
    container: {
      display: 'flex',
      flexDirection: 'column',
      height: '100vh',
      overflow: 'auto',
      backgroundImage:
        "url('https://mdn.alipayobjects.com/yuyan_qk0oxh/afts/img/V-_oS6r-i7wAAAAAAAAAAAAAFl94AQBr')",
      backgroundSize: '100% 100%',
    },
  };
});
const ActionIcons = () => {
  const { styles } = useStyles();
  return (
    <>
      <AlipayCircleOutlined key="AlipayCircleOutlined" className={styles.action} />
      <TaobaoCircleOutlined key="TaobaoCircleOutlined" className={styles.action} />
      <WeiboCircleOutlined key="WeiboCircleOutlined" className={styles.action} />
    </>
  );
};
const Login: React.FC = () => {
  const { initialState, setInitialState } = useModel('@@initialState');
  if (initialState?.currentUser) {
    history.push('/');
    return;
  }
  const [loginType, setLoginType] = useState<'PASSWD' | 'CODE'>('PASSWD');
  const { styles } = useStyles();
  const handleSubmit = async (values: API.UserLoginReq | any) => {
    let args: API.UserLoginReq = { loginType };
    if (loginType === 'CODE') {
      args.email = values.email as string;
      args.captcha = values.captcha as string;
    } else {
      if (values.account.includes('@')) {
        args.email = values.account as string;
      } else {
        args.username = values.account as string;
      }
      args.passwd = values.passwd as string;
    }
    const resp = await login(args);
    setInitialState((s) => ({
      ...s,
      currentUser: resp.data,
    }));
    history.push('/');
  };
  return (
    <div className={styles.container}>
      <Helmet>
        <title>
          {'登录'}- {Settings.title}
        </title>
      </Helmet>
      <div
        style={{
          flex: '1',
          padding: '32px 0',
        }}
      >
        <LoginForm
          contentStyle={{
            minWidth: 280,
            maxWidth: '75vw',
          }}
          logo={<img alt="logo" src="/logo.svg" />}
          title="API Platform"
          subTitle={'API Platform 致力于方便、高效的API'}
          initialValues={{
            autoLogin: false,
          }}
          actions={['其他登录方式 :', <ActionIcons key="icons" />]}
          onFinish={handleSubmit}
        >
          <Tabs
            activeKey={loginType}
            onChange={(val) => val === 'PASSWD' ? setLoginType('PASSWD') : setLoginType('CODE')}
            centered
            items={[
              {
                key: 'PASSWD',
                label: '用户名/邮箱密码登录',
              },
              {
                key: 'CODE',
                label: '邮箱验证码登录',
              },
            ]}
          />

          {/*{status === 'error' && loginType === 'account' && (*/}
          {/*  <LoginMessage content={'错误的用户名和密码(admin/ant.design)'} />*/}
          {/*)}*/}
          {loginType === 'PASSWD' && (
            <>
              <ProFormText
                name="account"
                fieldProps={{
                  size: 'large',
                  prefix: <UserOutlined />,
                }}
                placeholder={'用户名或者邮箱'}
                rules={[
                  {
                    required: true,
                    message: '用户名或者邮箱是必填项！',
                    pattern:
                      /(^\w+$)|(^[A-Za-z0-9\u4e00-\u9fa5]+@[a-zA-Z0-9][-a-zA-Z0-9]{0,62}(?:\.[a-zA-Z0-9][-a-zA-Z0-9]{0,62})+$)/,
                  },
                ]}
              />
              <ProFormText.Password
                name="passwd"
                fieldProps={{
                  size: 'large',
                  prefix: <LockOutlined />,
                }}
                placeholder={'密码'}
                rules={[
                  {
                    required: true,
                    message: '密码是必填项！',
                  },
                  {
                    message: '密码长度至少为5',
                    min: 5,
                  },
                ]}
              />
            </>
          )}

          {/*{status === 'error' && loginType === 'mobile' && <LoginMessage content="验证码错误" />}*/}
          {loginType === 'CODE' && (
            <>
              <ProFormText
                fieldProps={{
                  size: 'large',
                  prefix: <MailOutlined />,
                }}
                name="email"
                placeholder={'请输入邮箱'}
                rules={[
                  {
                    required: true,
                    message: '邮箱是必填项！',
                  },
                  {
                    pattern:
                      /^[A-Za-z0-9\u4e00-\u9fa5]+@[a-zA-Z0-9][-a-zA-Z0-9]{0,62}(?:\.[a-zA-Z0-9][-a-zA-Z0-9]{0,62})+$/,
                    message: '不合法的邮箱！',
                  },
                ]}
              />
              <ProFormCaptcha
                phoneName={'email'}
                fieldProps={{
                  size: 'large',
                  prefix: <LockOutlined />,
                }}
                captchaProps={{
                  size: 'large',
                }}
                placeholder={'验证码,区分大小写'}
                captchaTextRender={(timing, count) => {
                  if (timing) {
                    return `${count} ${'秒后重新获取'}`;
                  }
                  return '获取验证码';
                }}
                name="captcha"
                rules={[
                  {
                    required: true,
                    message: '验证码是必填项！',
                  },
                  {
                    message: '验证码长度为6',
                    len: 6,
                  },
                ]}
                onGetCaptcha={async (email) => {
                  await getCaptcha({
                    email,
                  });
                }}
              />
            </>
          )}
          <div
            style={{
              marginBottom: 24,
            }}
          >
            <Link to={'/user/register'}>
              还没账号？
            </Link>
            <Link
              to={'/user/passwd'}
              style={{
                float: 'right',
              }}
            >
              忘记密码 ?
            </Link>
          </div>
        </LoginForm>
      </div>
      <Footer />
    </div>
  );
};
export default Login;
