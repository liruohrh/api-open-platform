export default [
  {
    path: '/user',
    layout: false,
    routes: [
      { name: '登录', path: '/user/login', component: './User/Login' },
      { name: '注册', path: '/user/register', component: './User/Register' },
      { name: '重置密码', path: '/user/passwd', component: './User/Passwd' },
    ],
  },
  { path: '/welcome', name: '主页', icon: 'smile', component: './Welcome' },
  { name: '接口广场', icon: 'reddit', path: '/api/list', component: './API/List' },
  { path: '/api/:id', component: './API/$id' },
  { name: '我的订单', icon: 'reddit', path: '/order/list', component: './Order/List' },
  { path: '/order/:id', component: './Order/$id' },
  { path: '/order/pay/:id', component: './Order/Pay/$id' },
  {
    path: '/admin',
    name: '管理页',
    icon: 'crown',
    access: 'canAdmin',
    routes: [
      // { path: '/admin', redirect: '/admin/sub-page' },
      { path: '/admin/demo', name: '管理Demo', component: './Admin/Demo' },
      { path: '/admin/user', name: '管理用户', component: './Admin/User' },
      { path: '/admin/api', name: '管理API', component: './Admin/API' },
    ],
  },
  //无name就不显示侧边栏菜单
  { path: '/account/center', component: './Account/Center' },
  { path: '/', redirect: '/welcome' },
  { path: '*', layout: false, component: './404' },
];
