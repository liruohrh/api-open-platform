/**
 * @see https://umijs.org/docs/max/access#access
 * 1. 给路由添加属性access
 * 2. 路由的access属性值就是 access函数返回的对象key
 * */
export default function access(initialState: { currentUser?: API.User } | undefined) {
  const { currentUser } = initialState ?? {};
  return {
    canAdmin: currentUser?.role === 'ADMIN',
  };
}
