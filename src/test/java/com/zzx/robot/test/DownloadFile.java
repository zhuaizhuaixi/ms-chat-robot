package com.zzx.robot.test;

import org.apache.tomcat.util.http.fileupload.IOUtils;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * @author zzx
 * @date 2023/3/27
 */
public class DownloadFile {

    public static void main(String[] args) throws Exception {
        URL url = new URL("https://msavatar1.nexon.net/Character/MHJEEGHPLNENENIOCDELPBHJLIJJLOPMGBKBFHHLDBNMBNPIFKOBIFMPGCNGKLDJABEMAJOMOENOEJCPDAKOJGMDBKPNBIBFABPBDEFCLCKDKFEOFNAGOOOHFJLIBGDPCMAKBOBFHBMHLJIFKKGNOOJFKELMKPJBCGGNLCCLLHLFAHBBKJIMDHCMHINNLNMPOEDDOBNLAJNMDPHOALPMJEHDEBAFLHODODPGEBIPNDAKNOMFIBCJHBPFJMBJCNID.png");

        // 打开连接
        URLConnection con = url.openConnection();

        // 输入流
        InputStream is = con.getInputStream();

        FileOutputStream fos = new FileOutputStream("/Users/zzx/Downloads/角色卡.jpg");

        IOUtils.copyLarge(is, fos);

    }

}
