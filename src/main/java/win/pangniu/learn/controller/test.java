package win.pangniu.learn.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.FileUtils;

public class test {
	public static void main(String[] args) throws IOException {
		File configFile = new File("E:\\usr\\uploads\\abe1ef006bacf759354eaf061af773d9\\CentOS-7-x86_64-DVD-1511.iso.conf");
		byte[] completeList = FileUtils.readFileToByteArray(configFile);
		
		List<String> missChunkList = new LinkedList<String>();
		for (int i = 0; i < completeList.length; i++) {
			if (completeList[i] != Byte.MAX_VALUE) {
				missChunkList.add(i + "");
			}
		}
		//File toBeRenamed = new File("E:\\usr\\uploads\\abe1ef006bacf759354eaf061af773d9\\CentOS-7-x86_64-DVD-1511.iso_tmp");
//		Path source = Paths.get("E:\\usr\\uploads\\abe1ef006bacf759354eaf061af773d9\\CentOS-7-x86_64-DVD-1511.iso_tmp");
//		Path target = Paths.get("E:\\usr\\uploads\\abe1ef006bacf759354eaf061af773d9\\CentOS-7-x86_64-DVD-1511.iso");
		
//		 String p = toBeRenamed.getParent();
//	     File newFile = new File(p + File.separatorChar + "CentOS-7-x86_64-DVD-1511.iso");
//	        //修改文件名
//	     FileUtils.moveFile(toBeRenamed, newFile);
//	     Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
		System.out.println(completeList.length);
		System.out.println(missChunkList);
	}
}
