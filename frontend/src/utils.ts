import CryptoJS from 'crypto-js';

export function formatTimestamp(milis?: number) {
  if (milis === undefined) {
    return milis;
  }
  const date = new Date(milis);
  return (
    `${date.getFullYear()}-${date.getMonth() + 1}-${date.getDate()}` +
    ' ' +
    `${date.getHours()}:${date.getMinutes()}:${date.getSeconds()}`
  );
}

export function base64ToUtf8(base64Str: string) {
  return CryptoJS.enc.Base64.parse(base64Str).toString(CryptoJS.enc.Utf8);
}
export function emptyValueToNull(value: any): any {
  const newVal: any = {};
  Object.keys(value).forEach((key: string) => {
    if (!value[key] && value[key] !== false && value[key] !== 0) {
      return;
    }
    newVal[key] = value[key];
  });
  return newVal;
}
export function jsonPrettify(json?: string, space?: number): string {
  try {
    return JSON.stringify(JSON.parse(json || ''), null, space ? space : 4);
  } catch {
    return json || '';
  }
}
export function changeSortValue(sort: any): any {
  const newSort: any = {};
  Object.keys(sort).forEach((key) => {
    // @ts-ignore
    if (sort[key] === 'ascend') {
      newSort[key] = 'ASC';
    } else {
      newSort[key] = 'DESC';
    }
  });
  return newSort;
}

/**
 *
 * @param o1
 * @param o2
 * @return 返回o1和o2同属性值不同时o1的值
 */
export function getSamePropButValueNotEquals(o1: any, o2: any): any {
  const samePropButValueNotEquals = {};
  Object.keys(o1).forEach((key) => {
    // @ts-ignore
    if (o1[key] !== o2[key]) {
      // @ts-ignore
      samePropButValueNotEquals[key] = o1[key];
    }
  });
  return samePropButValueNotEquals;
}

export function localSort(data: any[], sort: any): any[] {
  for (const sortKey of Object.keys(sort)) {
    data?.sort((o1, o2) => {
      // @ts-ignore
      if (typeof o1[sortKey] === 'string') {
        // @ts-ignore
        return o1[sortKey].localeCompare(o2[sortKey]) * (sort[sortKey] === 'ascend' ? 1 : -1);
      } else {
        // @ts-ignore
        return (o1[sortKey] - o2[sortKey]) * (sort[sortKey] === 'ascend' ? 1 : -1);
      }
    });
  }
  return data;
}
