package win.pangniu.learn.controller;

import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import win.pangniu.learn.param.MultipartFileParam;
import win.pangniu.learn.service.StorageService;
import win.pangniu.learn.utils.Constants;
import win.pangniu.learn.vo.ResultStatus;
import win.pangniu.learn.vo.ResultVo;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.LinkedList;
import java.util.List;

@Controller
@RequestMapping(value = "/index")
public class IndexController {

	private Logger logger = LoggerFactory.getLogger(IndexController.class);

	@Autowired
	private StringRedisTemplate stringRedisTemplate;

	@Autowired
	private StorageService storageService;

	@Value("${breakpoint.upload.dir}")
	private String finalDirPath;

	/**
	 * 秒传判断，断点判断
	 *
	 * @return
	 */
	@RequestMapping(value = "checkFileMd5", method = RequestMethod.POST)
	@ResponseBody
	public Object checkFileMd5(String md5, String filename) throws IOException {

		// 文件上传后保存的目录
		String uploadDirPath = finalDirPath + md5;

		File parent = new File(uploadDirPath);
		// 文件不存在
		if (!parent.exists()) {
			return new ResultVo(ResultStatus.NO_HAVE);
		}

		File configFile = new File(uploadDirPath, filename + ".conf");
		File tmpFile = new File(uploadDirPath, filename + "_tmp");
		File targetFile = new File(uploadDirPath, filename);

		// 文件存在
		if (targetFile.exists()) {
			return new ResultVo(ResultStatus.IS_HAVE, targetFile.getAbsolutePath());
		} else if (configFile.exists() && tmpFile.exists()) {
			// 文件分块还没有上传完
			byte[] completeList = FileUtils.readFileToByteArray(configFile);
			System.out.println("config file content is :" + completeList.toString());
			List<String> missChunkList = new LinkedList<String>();
			for (int i = 0; i < completeList.length; i++) {
				if (completeList[i] != Byte.MAX_VALUE) {
					missChunkList.add(i + "");
				}
			}
			//所以分块已上传但是未合并
//			if(missChunkList.size()==0){
//				Path source = Paths.get(tmpFile.getAbsolutePath());
//				Path target = Paths.get(parent + File.separator + filename);
//				Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
//				if (configFile.exists()) {
//					configFile.delete();
//				}
//				return new ResultVo(ResultStatus.IS_HAVE, targetFile.getAbsolutePath());
//			}
			
			return new ResultVo<>(ResultStatus.ING_HAVE, missChunkList);
		} else {
			return new ResultVo<>(ResultStatus.ERROR);
		}

		
	}

	/**
	 * 上传文件
	 *
	 * @param param
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/fileUpload", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity fileUpload(MultipartFileParam param, HttpServletRequest request) {
		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		System.out.println(param.toString());
		if (isMultipart) {
			logger.info("上传文件start。");
			try {
				storageService.uploadFileByMappedByteBuffer(param);
			} catch (IOException e) {
				e.printStackTrace();
				logger.error("文件上传失败。{}", param.toString());
			}
			logger.info("上传文件end。");
		}
		return ResponseEntity.ok().body("上传成功。");
	}

}
