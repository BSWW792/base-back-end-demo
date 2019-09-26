package wtf.demo.core.util;

import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.servlet.ServletException;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 文件工具类
 * @author gongjf
 * @since 2019年3月9日 22:51:05
 */
@Component
@Slf4j
public class FileUtil {

    @Value("${system.test-environment}")
    private static boolean testEnvironment = false;

    public boolean isTestEnvironment() {
        return testEnvironment;
    }
    @Value("${system.test-environment}")
    public void setTestEnvironment(boolean testEnvironment) {
        FileUtil.testEnvironment = testEnvironment;
    }

    private static String rootPath;

    @Value("${file.upload.path}")
    public void setRootPath(String path) {
        rootPath = path;
    }

    /**
     * 上传文件
     */
    public static boolean saveFile(MultipartFile file, String filePath, String fileName) {
        if (DataUtil.isNotEmpty(file)) {
            fileName = DataUtil.isNotEmpty(fileName) ? fileName : file.getOriginalFilename();
            try {
                // 创建目标文件夹
                File targetFile = new File(createDir(rootPath + filePath), fileName);
                if (!targetFile.exists()) {
                    targetFile.createNewFile();
                }
                // 创建文件
                file.transferTo(targetFile);
                return true;
            } catch (IOException e) {
                log.error(e.getMessage());
                if(testEnvironment) e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 合并分片文件（要求文件夹内的文件的命名方式为 “文件夹名称_分片索引”）
     *
     * @param dir
     * @return
     */
    public static Boolean mergeChunkFile(File dir) {
        if (DataUtil.isEmpty(dir) || !dir.exists() || dir.isFile()) return false;

        File list[] = dir.listFiles();

        int chunkIndex = 0;
        int chunkCount = list.length;
        String sourcePath = dir.getAbsolutePath();
        String fileName = list[chunkIndex].getName();
        String ext = fileName.substring(fileName.lastIndexOf(".") + 1);
        fileName = fileName.substring(0, fileName.lastIndexOf("_"));
        String outFilePath = (new StringBuffer(sourcePath)).append(".").append(ext).toString();

        try {
            // 将分片文件写入目标文件
            @Cleanup
            FileChannel outChannel = new FileOutputStream(outFilePath, true).getChannel();
            for (; chunkIndex < chunkCount; chunkIndex++) {
                String path = (new StringBuffer(sourcePath)).append(File.separator).append(fileName).append("_").append(chunkIndex).append(".").append(ext).toString();
                @Cleanup
                FileChannel sourceChannel = new FileInputStream(path).getChannel();
                outChannel.transferFrom(sourceChannel, outChannel.size(), sourceChannel.size());
                sourceChannel.close();
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            if(testEnvironment) e.printStackTrace();
        } finally {
            // 分片文件合并完成
            if (chunkIndex == chunkCount) {
                return true;
            }
        }
        return false;
    }

    /**
     * 合并分片文件（要求文件夹内的文件的命名方式为 “文件夹名称_分片索引”）
     *
     * @param filePath
     * @return
     */
    public static Boolean mergeChunkFile(String filePath, String fileName) {
        if (DataUtil.isEmpty(filePath)) return false;
        return mergeChunkFile(new File(rootPath + filePath, fileName));
    }

    /**
     * 切割文件（文件夹内的文件的命名方式为 “文件夹名称_分片索引”）
     *
     * @param file
     * @param chunkCount
     * @return
     */
    public static Boolean cutFile(File file, int chunkCount) {
        if (DataUtil.isEmpty(file) || !file.exists() || file.isDirectory()
                || DataUtil.isEmpty(chunkCount) || chunkCount < 2) return false;

        int chunkIndex = 0;
        String sourcePath = file.getAbsolutePath();
        String outDirPath = sourcePath.substring(0, sourcePath.lastIndexOf("."));
        String fileName = outDirPath.substring(outDirPath.lastIndexOf(File.separator) + 1);
        String ext = sourcePath.substring(sourcePath.lastIndexOf(".") + 1);

        try {
            // 创建分片文件存放目录
            createDir(outDirPath);
            // 加载目标文件
            @Cleanup
            FileChannel sourceChannel = new FileInputStream(sourcePath).getChannel();
            // 计算分片大小
            Long chunkSize = sourceChannel.size() / chunkCount, start = new Long(0);
            // 将目标文件进行拆分
            for (; chunkIndex < chunkCount; chunkIndex++, start += chunkSize) {
                Long count = sourceChannel.size() - start < chunkSize ? sourceChannel.size() - start : chunkSize;
                // 写分片
                String path = (new StringBuffer(outDirPath)).append(File.separator).append(fileName).append("_").append(chunkIndex).append(".").append(ext).toString();
                @Cleanup
                FileChannel outChannel = new FileOutputStream(path, true).getChannel();
                sourceChannel.transferTo(start, count, outChannel);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            if(testEnvironment) e.printStackTrace();
        } finally {
            // 分片文件创建完成
            if (chunkIndex == chunkCount) {
                return true;
            }
        }
        return false;
    }

    /**
     * 切割文件（文件夹内的文件的命名方式为 “文件夹名称_分片索引”）
     *
     * @param filePath
     * @param chunkCount
     * @return
     */
    public static Boolean cutFile(String filePath, String fileName, int chunkCount) {
        if (DataUtil.isEmpty(filePath) || DataUtil.isEmpty(chunkCount) || chunkCount < 2) return false;
        return cutFile(new File(rootPath + filePath, fileName), chunkCount);
    }

    /**
     * 删除文件
     */
    public static boolean deleteFile(File file) {
        try {
            if (file.exists()) {
                return file.delete();
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            if(testEnvironment) e.printStackTrace();
        }
        return false;
    }

    /**
     * 删除文件
     */
    public static boolean deleteFile(String filePath, String fileName) {
        return deleteFile(new File(rootPath + filePath, fileName));
    }

    /**
     * 移动文件
     */
    public static void moveFile(File oldFile, File newFile) {
        copyFile(oldFile, newFile);
        deleteFile(oldFile);
    }

    /**
     * 移动文件
     */
    public static void moveFile(String oldDirPath, String newDirPath, String fileName) {
        copyFile(oldDirPath, newDirPath, fileName);
        deleteFile(oldDirPath, fileName);
    }

    /**
     * 复制文件
     *
     * @param source 源文件
     * @param dest   目标文件
     */
    public static void copyFile(File source, File dest) {
        try {
            @Cleanup
            FileChannel inputChannel = new FileInputStream(source).getChannel();
            @Cleanup
            FileChannel outputChannel = new FileOutputStream(dest).getChannel();
            outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
        } catch (Exception e) {
            log.error(e.getMessage());
            if(testEnvironment) e.printStackTrace();
        }
    }

    /**
     * 复制文件
     * oldDirPath: 原目录路径
     * newDirPath：新目录路径
     * fileName：文件名
     */
    public static void copyFile(String oldDirPath, String newDirPath, String fileName) {
        File oldFile = new File(rootPath + oldDirPath, fileName);
        File newFile = new File(rootPath + newDirPath, fileName);
        copyFile(oldFile, newFile);
    }

    /**
     * 递归删除目录下的所有文件及子目录下所有文件
     */
    public static boolean deleteDir(File dir) {
        if (!dir.exists()) {
            return false;
        }
        if (dir.isDirectory()) {
            String[] children = dir.list();
            //递归删除目录中的子目录下
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        // 目录此时为空，可以删除
        return dir.delete();
    }

    /**
     * 删除目录（文件夹）以及目录下的文件
     *
     * @param dirPath 被删除目录的文件路径
     * @return 目录删除成功返回true，否则返回false
     */
    public static boolean deleteDir(String dirPath) {
        //如果sPath不以文件分隔符结尾，自动添加文件分隔符
        if (!dirPath.endsWith(File.separator)) {
            dirPath += File.separator;
        }
        File dirFile = new File(rootPath + dirPath);
        //如果dir对应的文件不存在，或者不是一个目录，则退出
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            return false;
        }
        Boolean flag = true;
        //删除文件夹下的所有文件(包括子目录)
        File[] files = dirFile.listFiles();
        for (int i = 0; i < files.length; i++) {
            //删除子文件
            if (files[i].isFile()) {
                flag = deleteFile(files[i]);
                if (!flag) break;
            } //删除子目录
            else {
                flag = deleteDir(files[i]);
                if (!flag) break;
            }
        }
        if (!flag) return false;
        //删除当前目录
        if (dirFile.delete()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 文件重命名
     *
     * @param path    文件目录
     * @param oldName 原来的文件名
     * @param newName 新文件名
     */
    public static void renameFile(String path, String oldName, String newName) {
        // 新的文件名和以前文件名不同时,才有必要进行重命名
        if (!oldName.equals(newName)) {
            File oldfile = new File(rootPath + path, oldName);
            File newfile = new File(rootPath + path, newName);
            if (!oldfile.exists()) {
                return;
            }
            // 若在该目录下已经有一个文件和新文件名相同，则不允许重命名
            if (newfile.exists()) {
                System.out.println(newName + "已经存在！");
            } else {
                oldfile.renameTo(newfile);
            }
        } else {
            System.out.println("新文件名和旧文件名相同...");
        }
    }

    /**
     * 获取新的自定义文件名
     */
    public static String getNewName(String oldName) {
        StringBuffer newFileName = new StringBuffer(getTimeString(new Date(), "yyyyMMddHHmmss"));
        newFileName.append("_" + getRandomNum(8)); // 加入8位随机数

        if (DataUtil.isNotEmpty(oldName)) return newFileName.toString();

        String fileName = oldName.substring(0, oldName.indexOf(".")); // 截取原文件名
        String ext = oldName.substring(oldName.indexOf(".") + 1, oldName.length()); // 截取后缀名

        newFileName.append("." + ext);
        return fileName + "_" + newFileName.toString();
    }

    /**
     * 创建文件夹
     */
    public static String createDir(String path) {
        File dir = new File(rootPath + path);
        if (!dir.exists() && !dir.isDirectory())
            dir.mkdirs();
        return dir.getPath() + "/";
    }

    /**
     * 返回路径下的文件数
     */
    public static Integer getFileCount(String path) {
        File dir = new File(rootPath + path);
        File list[] = dir.listFiles();
        Integer count = 0;
        if (DataUtil.isEmpty(list)) return count;
        for (int i = 0; i < list.length; i++) {
            if (list[i].isFile()) {
                count++;
            }
        }
        return count;
    }

    /**
     * 返回路径下的目录数
     */
    public static Integer getDirCount(File dir) {
        if (DataUtil.isEmpty(dir) || !dir.exists() || dir.isFile()) return null;
        File list[] = dir.listFiles();
        Integer count = 0;
        if (DataUtil.isEmpty(list)) return count;
        for (int i = 0; i < list.length; i++) {
            if (list[i].isDirectory()) {
                count++;
            }
        }
        return count;
    }

    /**
     * 返回路径下的目录数
     */
    public static Integer getDirCount(String path) {
        File dir = new File(rootPath + path);
        return getDirCount(dir);
    }

    /**
     * 返回日期字符串
     */
    public static String getTimeString(Date date, String format) {
        Date d;
        if (date != null) d = date;
        else d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        String newFileName = sdf.format(d);
        return newFileName;
    }

    /**
     * 生成6位随机数
     */
    public static String getRandomNum(Integer $len) {
        String result = "";
        int len = $len != null ? $len : 6;
        Random random = new Random();
        for (int i = 0; i < len; i++) {
            result += random.nextInt(10);
        }
        return result;
    }

    /**
     * BASE64字符串转为二进制数据
     *
     * @param str
     * @return
     * @throws Exception
     */
    public static byte[] base64StrToByte(String str) {
        try {
            // Base64解码
            BASE64Decoder decoder = new BASE64Decoder();
            byte[] bytes = decoder.decodeBuffer(str);
            for (int i = 0; i < bytes.length; ++i) {
                // 调整异常数据
                if (bytes[i] < 0) {
                    bytes[i] += 256;
                }
            }
            return bytes;
        } catch (Exception e) {
            log.error(e.getMessage());
            if(testEnvironment) e.printStackTrace();
        }
        return null;
    }

    /**
     * 二进制数据转为BASE64字符串
     *
     * @param bytes
     * @return
     * @throws Exception
     */
    public static String byteToBase64Str(byte[] bytes) {
        BASE64Encoder encoder = new BASE64Encoder();
        return encoder.encode(bytes);
    }

    /**
     * 文件转Base64字符串
     * file：文件对象
     */
    public static String fileToBase64(File file) {
        if (DataUtil.isEmpty(file)) return "";
        byte[] bytes = null;
        try {
            @Cleanup
            InputStream in = new FileInputStream(file);
            bytes = new byte[in.available()];
            in.read(bytes);
        } catch (IOException e) {
            log.error(e.getMessage());
            if(testEnvironment) e.printStackTrace();
        }
        // 对字节数组Base64编码
        return byteToBase64Str(bytes);
    }

    /**
     * 文件转Base64字符串
     * path：文件目录（绝对路径）
     * name：文件名
     */
    public static String fileToBase64(String path, String name) {
        File file = new File(rootPath + path, name);
        return fileToBase64(file);
    }

    /**
     * 根据路径和名称判断是否存在文件夹
     *
     * @param path
     * @return
     */
    public static boolean checkDirExist(String path) {
        if (DataUtil.isEmpty(path)) return false;
        File file = new File(rootPath + path);
        return file.exists() && file.isDirectory();
    }

    /**
     * 根据路径和名称判断是否存在文件
     *
     * @param path
     * @param name
     * @return
     */
    public static boolean checkFileExist(String path, String name) {
        if (DataUtil.isEmpty(path) || DataUtil.isEmpty(name)) return false;
        File file = new File(rootPath + path, name);
        return file.exists() && file.isFile();
    }

    /**
     * Base64字符串转文件
     * str：base64字符串
     * path：文件目录（绝对路径）
     * name：文件名
     */
    public static Boolean base64ToFile(String str, String path, String name) {
        if (DataUtil.isEmpty(str)) return false;
        try {
            // 创建目标文件夹
            File targetFile = new File(createDir(rootPath + path), name);
            if (!targetFile.exists()) {
                targetFile.createNewFile();
            }

            // 生成文件
            @Cleanup
            OutputStream out = new FileOutputStream(targetFile);
            out.write(base64StrToByte(str));
            out.flush();
            out.close();
            return true;
        } catch (Exception e) {
            log.error(e.getMessage());
            if(testEnvironment) e.printStackTrace();
        }
        return false;
    }

    /**
     * 根据文件判断MIME类型
     * fileUrl：文件绝对路径，本地文件要附加 file://
     */
    public static String getMimeType(String fileUrl) {
        try {
            String type = null;
            URL u = new URL(fileUrl);
            URLConnection uc = null;
            uc = u.openConnection();
            type = uc.getContentType();
            return type;
        } catch (Exception e) {
            log.error(e.getMessage());
            if(testEnvironment) e.printStackTrace();
        }
        return "";
    }

    /**
     * 打包zip
     * @param fileUrls
     * @param filesName
     * @return
     */
    public static String downloadZip(List<String> fileUrls, String zipPath, String filesName) {
        if (fileUrls.size() < 1) {
            return null;
        }
        List<File> files = new ArrayList<File>();
        for (int i = 0; i < fileUrls.size(); i++) {
            File file = new File(rootPath + fileUrls.get(i));
            files.add(file);
        }
        String fileName = filesName + ".zip";
        String outFilePath = rootPath + zipPath;

        try {
            //如果文件已存在，不重新创建
            if (createFile(outFilePath, fileName)) {
                @Cleanup
                FileOutputStream outStream = new FileOutputStream(new File(outFilePath, fileName));
                @Cleanup
                ZipOutputStream zipOutputStream = new ZipOutputStream(outStream, Charset.forName("GBK"));
                zipFile(files, zipOutputStream);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            if(testEnvironment) e.printStackTrace();
        }

        // 文件路径
        return fileName;
    }

    /**
     * 创建文件,如果文件已存在，不创建
     *
     * @param path
     * @param fileName
     */
    public static boolean createFile(String path, String fileName) {
        boolean result = false;
        //path表示你所创建文件的路径
        File f = new File(rootPath + path);
        if (!f.exists()) {
            f.mkdirs();
        }
        // fileName表示你创建的文件名
        File file = new File(f, fileName);
        if (!file.exists()) {
            try {
                file.createNewFile();
                result = true;
            } catch (IOException e) {
                log.error(e.getMessage());
                if(testEnvironment) e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 压缩文件列表中的文件
     *
     * @param files
     * @param outputStream
     * @throws IOException
     * @throws ServletException
     */
    public static void zipFile(List<File> files, ZipOutputStream outputStream) {
        try {
            int size = files.size();
            // 压缩列表中的文件
            for (int i = 0; i < size; i++) {
                File file = (File) files.get(i);
                zipFile(file, outputStream);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            if(testEnvironment) e.printStackTrace();
        }
    }

    /**
     * 将文件写入到zip文件中
     *
     * @param inputFile
     * @param outputstream
     * @throws IOException
     * @throws ServletException
     */
    public static void zipFile(File inputFile, ZipOutputStream outputstream) {
        try {
            if (inputFile.exists()) {
                if (inputFile.isFile()) {
                    @Cleanup
                    FileInputStream inStream = new FileInputStream(inputFile);
                    @Cleanup
                    BufferedInputStream bInStream = new BufferedInputStream(inStream);
                    ZipEntry entry = new ZipEntry(inputFile.getName());
                    outputstream.putNextEntry(entry);


                    final int MAX_BYTE = 10 * 1024 * 1024; // 最大的流为10M
                    long streamTotal = 0; // 接受流的容量
                    int streamNum = 0; // 流需要分开的数量
                    int leaveByte = 0; // 文件剩下的字符数
                    byte[] inOutbyte; // byte数组接受文件的数据

                    streamTotal = bInStream.available(); // 通过available方法取得流的最大字符数
                    streamNum = (int) Math.floor(streamTotal / MAX_BYTE); // 取得流文件需要分开的数量
                    leaveByte = (int) streamTotal % MAX_BYTE; // 分开文件之后,剩余的数量

                    if (streamNum > 0) {
                        for (int j = 0; j < streamNum; ++j) {
                            inOutbyte = new byte[MAX_BYTE];
                            // 读入流,保存在byte数组
                            bInStream.read(inOutbyte, 0, MAX_BYTE);
                            outputstream.write(inOutbyte, 0, MAX_BYTE); // 写出流
                        }
                    }
                    // 写出剩下的流数据
                    inOutbyte = new byte[leaveByte];
                    bInStream.read(inOutbyte, 0, leaveByte);
                    outputstream.write(inOutbyte);
                    outputstream.closeEntry(); // 关闭当前zip entry
                }
            } else {
                log.warn("文件不存在！");
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            if(testEnvironment) e.printStackTrace();
        }
    }

    public static String readFileString(String filePath) {
        try {
            @Cleanup
            Reader reader = new InputStreamReader(new FileInputStream(new File(filePath)), "UTF-8");
            StringBuilder sb = new StringBuilder();
            int ch;
            while ((ch = reader.read()) != -1) {
                sb.append((char) ch);
            }
            return sb.toString().trim();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    public static boolean fileExists(String filePath, String fileName) {
        File file = new File(rootPath + filePath, fileName);
        return file.exists();
    }

    public static String getRootPath() {
        return rootPath;
    }

    public static void main(String[] args) {
        File file = new File("E:\\qwe.png");
        System.out.println(fileToBase64(file));
    }
}
