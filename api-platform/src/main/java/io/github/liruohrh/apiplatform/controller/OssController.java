package io.github.liruohrh.apiplatform.controller;

import cn.hutool.core.io.IoUtil;
import cn.hutool.crypto.digest.DigestUtil;
import io.github.liruohrh.apicommon.error.ParamException;
import io.github.liruohrh.apiplatform.common.util.MustUtils;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;

@RequestMapping("/oss")
@Controller
public class OssController {
  private final String port;
  private final RedisTemplate<Object, Object> redisTemplate;

  public OssController(
  @Value("${server.port}") String port,RedisTemplate<Object, Object> redisTemplate

  ) {
    this.port = port;
    this.redisTemplate = redisTemplate;
  }


  /**
   *
   * @param file 存在则直接返回
   * @return
   * @throws IOException
   */
  @ResponseBody
  @PostMapping
  public String upload(@RequestPart("file") Part file, HttpServletRequest req) throws IOException {
    String url;
    String contentType = file.getContentType();
    if(contentType.startsWith("image/")){
      if(!contentType.endsWith("jpeg") && !contentType.endsWith("png")){
        throw new ParamException("不支持 jpeg/png外的图片");
      }
      url = addImg(file.getInputStream(), contentType.replace("image/", ""), "http://127.0.0.1:" + port);
    }else{
      throw new ParamException("不支持 " + contentType);
    }
    return url;
  }

  private static final String IMAGES_DIR_NAME = "imgs";

  private static String addImg(InputStream inputStream, String type, String baseURL) throws IOException {
    byte[] bytes = IoUtil.readBytes(inputStream);
    String md5Hex = DigestUtil.md5Hex(bytes);
    String filename = md5Hex + "." + type;
    Path path = Paths.get(ROOT_DIR, IMAGES_DIR_NAME, filename);
    if (path.toFile().exists()) {
      return baseURL + "/oss/static/" + IMAGES_DIR_NAME + "/" + filename;
    }
    Files.write(path, bytes);
    return baseURL + "/oss/static/" + IMAGES_DIR_NAME + "/" + filename;
  }

  private static String getRootDir() {
    Path rootDir = Paths.get(System.getProperty("user.home"), ".easyapi");
    File file = rootDir.toFile();
    if (!file.exists()) {
      MustUtils.mustTrue(
          Paths.get(System.getProperty("user.home"), ".easyapi", "imgs").toFile().mkdirs(),
          "创建本地OSS的rootDir失败"
      );
    }
    return rootDir.toString();
  }

  public static final String ROOT_DIR = getRootDir();
}
