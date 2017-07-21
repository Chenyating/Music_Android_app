
package jimihua.utils;

import android.os.Environment;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class StringUtil {

    public static final String SAPCE_REGEX = " ";

    /**
     * Trim any trailing nul-chars
     * 
     * @param input The string to trim.
     * @return The 'input' without any trailing nul-chars.
     */
    public static String trimTrailingNul(String input) {
        if (input == null) {
            return null;
        }

        int i = input.length() - 1;
        while (i >= 0) {
            if (input.charAt(i) != '\0') {
                break;
            }
            i--;
        }

        return input.substring(0, i + 1);
    }

//    public static String trim(String str) {
//        if (StringUtil.isEmpty(str)) {
//            return str;
//        }
//        return innerTrim(str);
//    }
//
//    private static String innerTrim(String str) {
//        return str.replaceAll(SAPCE_REGEX, "");
//    }

    /**
     * Check if the string is empty
     * 
     * @param input string to be checked
     * @return true if input is not null and length>0, otherwise false
     */
    public static boolean isEmpty(String input) {
        return input == null || input.length() == 0;
    }

    /**
     * 判断是不是手机号
     * @param mobiles
     * @return
     *//*
    public static boolean isMobile(String mobiles){

        Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");

        Matcher m = p.matcher(mobiles);

        return m.matches();

    }*/

    /**
     * 手机号验证
     *
     * @param  str
     * @return 验证通过返回true
     */
    public static boolean isMobile(String str) {
        Pattern p = null;
        Matcher m = null;
        boolean b = false;
        p = Pattern.compile("^[1][3,4,5,7,8][0-9]{9}$"); // 验证手机号
        m = p.matcher(str);
        b = m.matches();
        return b;
    }
    /**
     * 电话号码验证
     *
     * @param  str
     * @return 验证通过返回true
     */
    public static boolean isPhone(String str) {
        Pattern p1 = null,p2 = null;
        Matcher m = null;
        boolean b = false;
        p1 = Pattern.compile("^[0][1-9]{2,3}-[0-9]{5,10}$");  // 验证带区号的
        p2 = Pattern.compile("^[1-9]{1}[0-9]{5,8}$");         // 验证没有区号的
        if(str.length() >9)
        {   m = p1.matcher(str);
            b = m.matches();
        }else{
            m = p2.matcher(str);
            b = m.matches();
        }
        return b;
    }


    /**
     * �ο�PhoneNumberUtils.isUriNumber
     * 
     * @param number the number for check
     * @return true if the string is a URI number
     */
    public static boolean isUriNumber(String number) {
        // Neither "@" nor "%40" will ever be found in a legal PSTN number.)
        return number != null && (number.contains("@") || number.contains("%40"));
    }

    /**
     * it's a alternative solution to return the name of a URI number
     * 
     * @param number the URI number
     * @return the name of URI number
     */
    public static String getUriName(String number) {
        String name = null;

        int at = number.indexOf("@");
        if (at == -1) {
            at = number.indexOf("%40");
        }

        if (at != -1) {
            name = number.substring(0, at);
        }

        return name;
    }

    public static final void writeUTF(DataOutputStream dos, String string) throws IOException {
        if (string == null) {
            dos.writeUTF("");
        } else {
            dos.writeUTF(string);
        }
    }


    public static String md5ToHex(String in) {
        MessageDigest digest;
        StringBuffer result = new StringBuffer();
        try {
            digest = MessageDigest.getInstance("MD5");
            digest.reset();
            digest.update(in.getBytes());
            byte[] a = digest.digest();
//            int len = a.length;
            for (int i = 0; i < a.length; i++) {
                result.append(byteToHex(a[i] & 0xFF));
            }

            return result.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String byteToHex(int b) {
        return byteToHex(new StringBuilder(), b).toString();
    }

    public static StringBuilder byteToHex(StringBuilder sb, int b) {
        b &= 0xFF;
        sb.append("0123456789ABCDEF".charAt(b >> 4));
        sb.append("0123456789ABCDEF".charAt(b & 0xF));
        return sb;
    }
    
    public static String compressByGzip(String str) {
        String ret = null;
        ByteArrayOutputStream os = null;
        GZIPOutputStream gos = null;
        try {
            os = new ByteArrayOutputStream(str.length());
            gos = new GZIPOutputStream(os);
            gos.write(str.getBytes());
            gos.finish();
//            gos.flush();//在android4.4上此语句报错
            ret = Base64.encodeToString(os.toByteArray(), Base64.DEFAULT);
        } catch(Exception e) {
            Log.e("compress", "IOException: ", e);
        } finally {
            if (gos != null) {
                try {
                    gos.close();
                } catch (IOException e) {
                    Log.e("compress", "gos : ", e);
                }
            }
            
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    Log.e("compress", "os : ", e);
                }
            }
        }
        
        return ret;
    }
    
    public static String decompressByGzip(String zipText) {
        
        String ret = null;
        
        byte[] compressed = Base64.decode(zipText, Base64.NO_WRAP);
        GZIPInputStream gzipInputStream = null;
        ByteArrayOutputStream baos = null;
        
        try {
            gzipInputStream = new GZIPInputStream(
                    new ByteArrayInputStream(compressed, 0, compressed.length));
            baos = new ByteArrayOutputStream();
            
            byte[] buf = new byte[1024];
            int count = 0;
            while ((count = gzipInputStream.read(buf)) != -1) {
                baos.write(buf, 0, count);
            }
            ret = new String(baos.toByteArray(), "UTF-8");
        } catch (IOException e) {
            Log.e("decompress", "IOException", e);
        } finally {
            if (gzipInputStream != null) {
                try {
                    gzipInputStream.close();
                } catch (IOException e) {
                    Log.e("decompress", "gzip", e);
                }
            }
            
            if (baos != null) {
                try {
                    baos.close();
                } catch (IOException e) {
                    Log.e("decompress", "baos", e);
                }
            }
        }
        
        return ret;
    }

    public static Object objectWithString(String value) {
        if (StringUtil.isEmpty(value)) return null;

        try {
            String decoderValue = URLDecoder.decode(value, "UTF-8");
            ByteArrayInputStream bais = new ByteArrayInputStream(
                    decoderValue.getBytes("ISO-8859-1"));
            ObjectInputStream ios = new ObjectInputStream(bais);
            return ios.readObject();
        } catch (UnsupportedEncodingException e1) {
            // Auto-generated catch block
            e1.printStackTrace();
        } catch (StreamCorruptedException e) {
            // Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // Auto-generated catch block
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            // Auto-generated catch block
            e.printStackTrace();
        }
        
        return null;
    }

    public static String stringWithObject(Object object) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos;
        try {
            oos = new ObjectOutputStream(baos);
            oos.writeObject(object);
            String value = baos.toString("ISO-8859-1");
            String encodedValue = URLEncoder.encode(value, "UTF-8");
            return encodedValue;
        } catch (IOException e) {
            // Auto-generated catch block
            e.printStackTrace();
        }
        return null;
  
    }
    


    public static String filterUnNumber(String str) {
        String regEx = "[^0-9]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.replaceAll("").trim();  
    }  
    
//    private static final Pattern IS_DIGITS_PATTERN = Pattern.compile("[\\+]?[0-9.]+");
    public static boolean isNumeric(String str) {
        str = str.replaceAll(SAPCE_REGEX, "");
        // upto length 13 and including character + infront. ^\+[0-9]{10,13}$
        // ^[+]?[0-9]{10,13}
        return str.matches("^[\\+0-9]\\d*$");
        // return str.matches("^[\\+0-9]*[1-9][0-9]*$");
    }
    
    /**
     * Function that get the size of an object.
     * 
     * @param object
     * @return Size in bytes of the object or -1 if the object
     * is null.
     * @throws IOException
     */
    public static final int sizeOf(Object object) throws IOException {
      
      if (object == null)
        return -1;
      
      // Special output stream use to write the content
      // of an output stream to an internal byte array.
      ByteArrayOutputStream byteArrayOutputStream =
        new ByteArrayOutputStream();
      
      // Output stream that can write object
      ObjectOutputStream objectOutputStream =
        new ObjectOutputStream(byteArrayOutputStream);
      
      // Write object and close the output stream
      objectOutputStream.writeObject(object);
      objectOutputStream.flush();
      objectOutputStream.close();
      
      // Get the byte array
      byte[] byteArray = byteArrayOutputStream.toByteArray();
      
      // can the toByteArray() method return a
      // null array ?
      return byteArray == null ? 0 : byteArray.length;
      
    }
    
    public static boolean isMobilePhoneInChina(String phoneNumber) {
        
        /*
         * 手机：^(1(([35][0-9])|(47)|[8][01236789]))\d{8}$
         * ^(13[0-9]|147|15[0|3|6|7|8|9]|18[0|8|9])\d{8}$
            座机：^0\d{2,3}(\-)?\d{7,8}$  
         */
        return phoneNumber.matches("^((\\+86)|(86)){0,1}(13[0-9]|147|15[0-9]|18[0-9])\\d{8}$");
    }

    public static boolean isLandlinePhoneInChina(String phoneNumber){
        return phoneNumber.matches("^0\\d{2,3}(\\-)?\\d{7,8}$");
    }

    public static String getStringDate() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
        return format.format(date);
    }
    

    public static String ensureEmptyStr(String str) {
        return str == null ? "" : str;
    }
    
    public static String formatTime(long time){
        long days = 0L;
        long hours = 0L;
        long mins = 0L;
        long totalDuration = time;
        mins = totalDuration / 60;
        if (totalDuration / 60 > 0) {
            if (mins / 60 > 0) {
                hours = mins / 60;
                if (hours / 24 > 0) {
                    days = hours / 24;
                    return days + "天," + hours + "小时, " + mins + "分钟, " + totalDuration % 60
                            + "秒";
                } else {
                    return hours + "小时, " + mins % 60 + "分钟, " + totalDuration % 60 + "秒";
                }
            } else {
                return mins + "分钟, " + totalDuration % 60 + "秒";
            }
        } else {
            return totalDuration + "秒";
        }
    }


    /* if (url.contains("group://")) {
                    String arg1 = (url.split("\\?"))[1];
                    if (!StringUtil.isEmpty(arg1)) {
                        String[] temp = arg1.split("&");
                        tagId = (temp[0].split("="))[1];

                        String[] tempTag = temp[1].split("=");
                        if(tempTag.length == 2){
                            tagName = URLDecoder.decode(tempTag[1]).trim();
                        }else{
                            tagName = mGroupName;
                        }
                        Intent intent = new Intent();
                        intent.putExtra(Constants.INTENT_TAG_ID, tagId);
                        intent.putExtra(Constants.INTENT_TAG_NAME, tagName);
                        setResult(RESULT_OK, intent);
//                        getCloudContactList(Long.parseLong(gId), mCurrentPage ,mCloudHandler.obtainMessage(MSG_CONTACT_GETLIST), tagId);
                    }*/

    public static Map<String, String> analysisUrl(String url){
        Map<String, String> result = new HashMap<String, String>();
        String key = "";
        String value = "";
        if(!StringUtil.isEmpty(url)){
            String content = (url.split("\\?"))[1];
            String[] temp = content.split("&");
            for (int i = 0; i < temp.length; i++) {
                String[] keyANDvalue = temp[i].split("=");
                if(keyANDvalue.length == 2){
                    key = keyANDvalue[0];
                    value = keyANDvalue[1];
                    result.put(key, value);
                }else if(keyANDvalue.length == 1){
                    key = keyANDvalue[0];
                    value = "";
                    result.put(key, value);
                }
            }
        }
        return result;
    }

    /**
     * 将byte数组通过GZIP压缩后用base64转码成字符串
     * @param byteArr
     * @return
     */
    public static String compressByteArrayByGzip(byte[] byteArr){
        String base64String = null;
        ByteArrayOutputStream os = null;
        GZIPOutputStream gos = null;
        try {
            os = new ByteArrayOutputStream();
            gos = new GZIPOutputStream(os);
            gos.write(byteArr);
            gos.finish();
            //gos.flush();//android 4.4报错
            base64String = Base64.encodeToString(os.toByteArray(), Base64.DEFAULT);
        } catch(Exception e) {
            Log.e("compress", "IOException: ", e);
        } finally {
            if (gos != null) {
                try {
                    gos.close();
                } catch (IOException e) {
                    Log.e("compress", "gos : ", e);
                }
            }

            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    Log.e("compress", "os : ", e);
                }
            }
        }
        return base64String;
    }

    /**
     * base64反解析图片流后Gzip解压
     * @param zipText
     * @return
     */
    public static byte[] decompressToByteArrayByGzip(String zipText) {
        byte[] byteArr = null;
        byte[] compressed;
        try {
            compressed = Base64.decode(zipText, Base64.NO_WRAP);
        }catch (OutOfMemoryError e){
            return byteArr;
        }catch (Exception e){
            //参数错误
            return byteArr;
        }
        GZIPInputStream gzipInputStream = null;

        ByteArrayOutputStream baos = null;
        try {
            gzipInputStream = new GZIPInputStream(
                    new ByteArrayInputStream(compressed, 0, compressed.length));
            baos = new ByteArrayOutputStream();

            byte[] buf = new byte[1024];
            int count = 0;
            while ((count = gzipInputStream.read(buf)) != -1) {
                baos.write(buf, 0, count);
            }
            byteArr = baos.toByteArray();
        } catch (IOException e) {
            Log.e("decompress", "IOException", e);
        } finally {
            if (gzipInputStream != null) {
                try {
                    gzipInputStream.close();
                } catch (IOException e) {
                    Log.e("decompress", "gzip", e);
                }
            }

            if (baos != null) {
                try {
                    baos.close();
                } catch (IOException e) {
                    Log.e("decompress", "baos", e);
                }
            }
        }
        return byteArr;
    }

    /**
     * 将文件转成base64字符串(用于发送语音文件)
     * @param path
     * @return
     * @throws Exception
     */
    public static String encodeBase64File(String path) throws Exception {
        File file = new File(path);
        FileInputStream inputFile = new FileInputStream(file);
        byte[] buffer = new byte[(int)file.length()];
        inputFile.read(buffer);
        inputFile.close();
        return Base64.encodeToString(buffer, Base64.DEFAULT);
    }

    /**
     * 将base64编码后的字符串转成文件
     * @param base64Code
     * @throws Exception
     */
    public static String decoderBase64File(String base64Code)  {
        String savePath = "";
        String saveDir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Yun/Sounds/";
        File dir = new File(saveDir);
        if(!dir.exists()){
            dir.mkdirs();
        }
        savePath = saveDir+getRandomFileName();
        byte[] buffer = Base64.decode(base64Code, Base64.DEFAULT);
        try {
            FileOutputStream out = new FileOutputStream(savePath);
            out.write(buffer);
            out.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return  savePath;
    }

    public static String getRandomFileName() {
        String rel="";
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        Date curDate = new Date(System.currentTimeMillis());
        rel = formatter.format(curDate);
        rel = rel + new Random().nextInt(1000);
        return rel+".amr";
    }


}
