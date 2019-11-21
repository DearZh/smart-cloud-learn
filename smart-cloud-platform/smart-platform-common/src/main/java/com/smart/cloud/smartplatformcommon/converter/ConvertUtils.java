/*
 * Power by www.xiaoi.com
 */
package com.smart.cloud.smartplatformcommon.converter;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;

/*import org.mozilla.universalchardet.UniversalDetector;*/

@Component
public class ConvertUtils {

    private static final Logger log = LoggerFactory.getLogger(ConvertUtils.class);

    /**
     * pptx & ppt 文件是否转换成 pdf，默认转成pdf，pdf预览样式效果优秀于 html
     */
    private static final Boolean CONVERT_PPT_TO_PDF = true;

    /**
     * 文件本地路径，从sftp下载后的存储目录
     * e.g. /home/xiaoi/file/DATAS
     */
    private static String SOURCE_DIR_PATH = null;//CommonProperties.getSftp().getDir();
    /**
     * e.g. /home/xiaoi/jetty-converter-7220/webapps/kbase-converter/DATAS
     */
    private static String targetDirPath;
    private static String SWF_OUTPUT_PATH;
    private static String HTML_OUTPUT_PATH;
    private static String PDF_OUTPUT_PATH;

    static {
        //获取当前文件存储路径
        String rootPath = "";//CommonProperties.getSftp().getDir();
        targetDirPath = rootPath + "DATAS" + File.separator;
        SWF_OUTPUT_PATH = targetDirPath + "swf";
        HTML_OUTPUT_PATH = targetDirPath + "html";
        PDF_OUTPUT_PATH = targetDirPath + "pdf";

        // 如果目标目录不存在则创建
        new File(SWF_OUTPUT_PATH).mkdirs();
        new File(HTML_OUTPUT_PATH).mkdirs();
        new File(PDF_OUTPUT_PATH).mkdirs();
    }

    /**
     * 去除首位的 /
     *
     * @param path
     * @return
     * @author eko.zhan at Sep 11, 2018 1:51:37 PM
     */
    public static String trimSlash(String path) {
        path = leftSlash(path);
        path = rightSlash(path);
        return path;
    }

    /**
     * 移除左边的 /
     *
     * @param path
     * @return
     * @author eko.zhan at Sep 11, 2018 4:12:32 PM
     */
    public static String leftSlash(String path) {
        path = backslash(path);
        if (StringUtils.startsWith(path, "/")) {
            path = path.substring(1);
        }
        return path;
    }

    /**
     * 移除右边的 /
     *
     * @param path
     * @return
     * @author eko.zhan at Sep 11, 2018 4:23:26 PM
     */
    public static String rightSlash(String path) {
        path = backslash(path);
        if (StringUtils.endsWith(path, "/")) {
            path = path.substring(0, path.length() - 1);
        }
        return path;
    }

    /**
     * 替换所有反斜杠
     *
     * @param path
     * @return
     * @author eko.zhan at Sep 11, 2018 1:55:14 PM
     */
    public static String backslash(String path) {
        return path.replace("\\", "/");
    }

    /**
     * 获取文件编码，全局统一
     *
     * @param file
     * @return java.lang.String
     * @author eko.zhan
     * @date 2018/12/24 13:52
     */
    public static String getFileEncoding(File file) {
        /*UniversalDetector detector = new UniversalDetector(null);*/
        /*try {
            byte[] bytes = FileUtils.readFileToByteArray(file);
            detector.handleData(bytes, 0, bytes.length);
            detector.dataEnd();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String encoding = detector.getDetectedCharset();
        encoding = StringUtils.isNotBlank(encoding) ? encoding : "GB2312";
        if ("GB18030".equals(encoding) || "KOI8-R".equals(encoding)) {
            encoding = "GB2312";
        }*/
        return "GB2312";
    }

    /**
     * <pre>
     * 获取源文件理论上应该转换成的目标文件， 转换成html方式，office，txt文件的路径应该在html目录下，pdf在pdf目录，其余的在swf目录
     * </pre>
     */
    public static String getConvertTargetFilePath(String sourceFilePath) {
        String releatePath = sourceFilePath.substring(SOURCE_DIR_PATH.length(), sourceFilePath.lastIndexOf("."));

        if (CONVERT_PPT_TO_PDF && FileExtensionUtils.isPpt(sourceFilePath)) {
            return PDF_OUTPUT_PATH + File.separator + releatePath + ".pdf";
        }

        if (FileExtensionUtils.isOffice(sourceFilePath) || FileExtensionUtils.isTxt(sourceFilePath)) {
            File file = new File(sourceFilePath);
            String prefix = FilenameUtils.getBaseName(file.getName());

            return HTML_OUTPUT_PATH + File.separator + releatePath + File.separator + prefix + ".html";
        } else if (FileExtensionUtils.isPdf(sourceFilePath)) {
            return PDF_OUTPUT_PATH + File.separator + releatePath + ".pdf";
        } else {
            return SWF_OUTPUT_PATH + File.separator + releatePath + "." + FilenameUtils.getExtension(sourceFilePath);
        }
    }

    public static String getTargetDirPath() {
        return targetDirPath;
    }

    public static String getSourceDirPath() {
        return SOURCE_DIR_PATH;
    }

    /**
     * 根据源文件名称获取目标文件路径，如果未找到返回null
     * <p>
     * 兼容老版本，如果fileName无后缀，则默认获取swf文件
     * </p>
     * <p>
     * 为什么要传入源文件名称？在之前的版本中，文件只转换成swf，所以只需要在DATAS目录下获取到swf文件即是目标文件文件路径，
     * 根据实际中的需求，文件不仅仅是转换成swf，还可能转换成html格式，pdf格式，根据源文件格式来判断，所以需要传入源文件参数
     * </p>
     *
     * @param srcFileName 源文件名称，例如9527.docx，那么如果当前模块转html，则对应的目标文件名是9527.html
     *                    如果当前模块转pdf，
     *                    则对应的目标文件名是9527.pdf，如果当前模块转swf，则对应的目标文件名是9527.swf，同理9527
     *                    .pdf-->9527.swf
     * @return 返回一个数组，第一项是文件所在的目录，如D:\\jetty-6.1.26\\webapps\\kbase-converter\\swf
     * \\html，第二项是目标文件名
     * @author eko.zhan
     * @since 2014-11-17
     */
    public static String[] getRealInfo(String srcFileName) {

        log.debug("getRealInfor.srcFilePath=" + srcFileName);

        // mode=html情况，针对ppt转pdf获取目标路径
        if (StringUtils.isNotBlank(srcFileName) && CONVERT_PPT_TO_PDF) {
            if (StringUtils.endsWith(srcFileName.toLowerCase(), "ppt") || StringUtils.endsWith(srcFileName, "pptx")) {
                String suffix = FilenameUtils.getExtension(srcFileName).toLowerCase();
                if (FileExtensionUtils.isOfficeOrPdf(srcFileName) || FileExtensionUtils.isTxt(srcFileName)) {
                    return new String[]{PDF_OUTPUT_PATH, FilenameUtils.getBaseName(srcFileName) + ".pdf"};
                } else {
                    return new String[]{SWF_OUTPUT_PATH, FilenameUtils.getBaseName(srcFileName) + "." + suffix};
                }
            }
        }

        String suffix = FilenameUtils.getExtension(srcFileName).toLowerCase();

        log.debug("getRealInfor.suffix=" + suffix);
        if (FileExtensionUtils.isOffice(srcFileName) || FileExtensionUtils.isHtml(srcFileName) || FileExtensionUtils.isTxt(srcFileName)) {
            log.debug("getRealInfor.html");
            return new String[]{HTML_OUTPUT_PATH, FilenameUtils.getBaseName(srcFileName) + ".html"};
        } else if (FileExtensionUtils.isPdf(srcFileName)) {
            log.debug("getRealInfor.pdf");
            return new String[]{PDF_OUTPUT_PATH, FilenameUtils.getBaseName(srcFileName) + ".pdf"};
        } else {
            log.debug("getRealInfor.swf");
            return new String[]{SWF_OUTPUT_PATH, FilenameUtils.getBaseName(srcFileName) + "." + suffix};
        }
    }

}
