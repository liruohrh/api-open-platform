/**
 * 网络地址验证工具函数
 */

// 懒加载的正则表达式模式
let INTEGER: RegExp | null = null;
let PHONE_NUMBER: RegExp | null = null;
let DOMAIN: RegExp | null = null;

export function getRegxOfInteger(): RegExp {
  if (!INTEGER) {
    INTEGER = /^\d+$/;
  }
  return INTEGER;
}
export function getRegxOfPhoneNumber(): RegExp {
  if (!PHONE_NUMBER) {
    PHONE_NUMBER = /^(?:(?:\+|00)86)?1[3-9]\d{9}$/;
  }
  return PHONE_NUMBER;
}
export function getRegxOfDomain(): RegExp {
  if (!DOMAIN) {
    DOMAIN = /^[a-zA-Z0-9][-a-zA-Z0-9]{0,62}(?:\.[a-zA-Z0-9][-a-zA-Z0-9]{0,62})+$/;
  }
  return DOMAIN;
}
/**
 * 校验手机号
 */
export function isPhoneNumber(str: string): boolean {
  if (!str) return false;
  return getRegxOfPhoneNumber().test(str);
}

/**
 * 校验邮箱格式
 * @param email 不校验用户名部分，只允许域名而不允许ip
 */
export function isEmail(email: string): boolean {
  if (!email) return false;
  const split = email.split('@');
  if (split.length !== 2) {
    return false;
  }
  return isDomain(split[1]);
}

/**
 * 校验域名格式
 * @param domain 仅校验字母、数字、-
 */
export function isDomain(domain: string): boolean {
  if (!domain) return false;
  const lastDotIndex = domain.lastIndexOf('.');
  //like docker container
  if (lastDotIndex === -1) return true;
  return (
    getRegxOfDomain().test(domain) && !getRegxOfInteger().test(domain.substring(lastDotIndex + 1))
  );
}

/**
 * 校验域名地址（含端口）
 */
export function isDomainAddr(domainAddr: string): boolean {
  if (!domainAddr) return false;
  let domain = domainAddr;
  const portI = domainAddr.lastIndexOf(':');

  if (portI !== -1) {
    domain = domainAddr.substring(0, portI);
    if (portI + 1 === domainAddr.length) {
      return false;
    }
    if (!isPort(domainAddr.substring(portI + 1))) {
      return false;
    }
  }
  return isDomain(domain);
}

/**
 * 校验端口号（数字形式）
 */
export function isPort(port: number | string): boolean {
  let portInt = -1;
  if (typeof port === 'string') {
    try {
      portInt = parseInt(port, 10);
      if (isNaN(portInt)) {
        return false;
      }
    } catch (e) {
      return false;
    }
  } else {
    portInt = port;
  }
  return portInt > 0 && portInt < 65536;
}

/**
 * 校验IP地址（含端口）
 */
export function isIpAddr(ipAddr: string): boolean {
  if (!ipAddr) return false;
  const split = ipAddr.split(/\.|:/) as string[];
  if (split.length !== 4 && split.length !== 5) {
    return false;
  }

  try {
    for (let i = 0; i < 4; i++) {
      const octet = parseInt(split[i], 10);
      if (isNaN(octet) || octet > 255 || octet < 0) {
        return false;
      }
    }
  } catch (e) {
    return false;
  }

  return split.length !== 5 || isPort(split[4]);
}

/**
 * 校验Socket地址（域名或IP地址，可含端口）
 */
export function isSocketAddr(addr: string): boolean {
  if (!addr) return false;
  return isDomainAddr(addr) || isIpAddr(addr);
}

/**
 * 校验URL格式
 */
export function isUrl(url: string): boolean {
  if (!url) return false;
  const pureUrl = url.trim();
  let socketAddr: string;

  if (pureUrl.startsWith('http://')) {
    socketAddr = pureUrl.substring('http://'.length);
  } else if (pureUrl.startsWith('https://')) {
    socketAddr = pureUrl.substring('https://'.length);
  } else {
    return false;
  }

  if (!socketAddr) {
    return false;
  }

  const firstSlashI = socketAddr.indexOf('/');
  if (firstSlashI !== -1) {
    socketAddr = socketAddr.substring(0, firstSlashI);
  }

  return isSocketAddr(socketAddr);
}

/**
 * 验证端口号，如果无效则抛出异常
 */
export function validatePort(port: number, error: string): void {
  if (port !== null && port !== undefined && !isPort(port)) {
    throw new Error(error);
  }
}

/**
 * 验证手机号，如果无效则抛出异常
 */
export function validatePhoneNumber(phone: string): void {
  if (phone && phone.trim() !== '' && !isPhoneNumber(phone)) {
    throw new Error('手机号不正确');
  }
}

/**
 * 验证邮箱，如果无效则抛出异常
 */
export function validateEmail(email: string): void {
  if (email && email.trim() !== '' && !isEmail(email)) {
    throw new Error('邮箱号不正确');
  }
}

/**
 * 验证URL，如果无效则抛出异常
 */
export function validateUrl(url: string, error: string): void {
  if (url && url.trim() !== '' && !isUrl(url)) {
    throw new Error(error);
  }
}
