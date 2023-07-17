package com.god.dragon.utils.common;

import com.itextpdf.awt.geom.AffineTransform;
import com.itextpdf.awt.geom.Rectangle2D;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.parser.*;
import lombok.extern.slf4j.Slf4j;


import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author wuyuxiang
 * @version 1.0.0
 * @package com.god.dragon.utils.common
 * @date 2023/7/17 10:18
 * @description TODO
 */
@Slf4j
public class PdfUtils {
    private static BaseFont bf;
    static{
        try{
            //初始化PDF写入的默认字体
            bf = BaseFont.createFont("STSong-Light","UniGB-UCS2-H",BaseFont.EMBEDDED);
        }catch(Exception e){
            log.error(e.getMessage(),e);
        }
    }

    /**
     * 创建PDF阅读器
     * @param in 文件输入流，pdf文件输入流
     * @param out 文件输出流，pdf修改后文件输出流
     * @return
     */
    public static PdfStamper createPdfStamper(InputStream in, OutputStream out){
        PdfReader pdfReader = null;
        try{
            pdfReader = new PdfReader(in);
            return new PdfStamper(pdfReader, out);
        }catch (Exception e){
            log.error("创建PDF读取器错误:"+e.getMessage(),e);
            if(pdfReader != null){
                pdfReader.close();
            }
            throw new RuntimeException("创建PDF阅读器异常");
        }
    }

    /**
     *
     * 创建PDF阅读器
     * @param in pdf文件字节数组
     * @param out 文件输出流，pdf修改后文件输出流
     * @return
     */
    public static PdfStamper createPdfStamper(byte[] in,OutputStream out){
        PdfReader pdfReader = null;
        try{
            pdfReader = new PdfReader(in);
            return new PdfStamper(pdfReader, out);
        }catch (Exception e){
            log.error("创建PDF读取器错误:"+e.getMessage(),e);
            if(pdfReader != null){
                pdfReader.close();
            }
            throw new RuntimeException("创建PDF阅读器异常");
        }
    }

    /**
     * 自定义添加文本
     * @param stamper 文件读取器
     * @param pageIndex pdf第几页
     * @param x 添加文本对应pdf某一页的x轴
     * @param y 添加文本对应pdf某一页的y轴
     * @param fontSize 添加字体大小
     * @param text 添加的文本内容
     */
    public static void appendText(PdfStamper stamper,int pageIndex,float x,float y,float fontSize,String text){
        PdfContentByte canvas = stamper.getOverContent(pageIndex);
        canvas.beginText();
        Font font = new Font(bf,fontSize, Font.BOLD); //选择字体格式,字体大小,字体样式(BOLD加粗)
        canvas.setFontAndSize(font.getBaseFont(), fontSize);
//        解决double->float精度问题
//        AffineTransform aff = new AffineTransform();
//        aff.setToTranslation(x,y);
        canvas.setTextMatrix(x,y);
        canvas.showText(text);
        canvas.endText();
    }

    /**
     * 自定义添加图片
     * @param stamper 文件读取器
     * @param pageIndex pdf第几页
     * @param x 添加文本对应pdf某一页的x轴
     * @param y 添加文本对应pdf某一页的y轴
     * @param content 图片字节数组
     */
    public static void appendImage(PdfStamper stamper,int pageIndex,float x,float y,byte[] content){
        PdfContentByte canvas = stamper.getOverContent(pageIndex);
        try {
            Image image = Image.getInstance(content);
            image.setAbsolutePosition(x, y);
            canvas.addImage(image);
        }catch (Exception e){
            log.error(e.getMessage(),e);
            throw new RuntimeException("PDF添加图片异常");
        }
    }

    /**
     * 关闭PDF阅读器
     * @param stamper
     */
    public static void closeReader(PdfStamper stamper){
        if(stamper == null){
            return;
        }

        try{
            stamper.close();
        }catch (Exception e){
            log.error("关闭PDF阅读器错误:"+e.getMessage(),e);
            throw new RuntimeException("关闭PDF阅读器异常");
        }finally {
            stamper.getReader().close();
        }
    }

    /**
     * 在PDF中找到特征值的左下坐标,例如 姓名：xxx,我要添加这个姓名,那特征值就是姓名
     * @param stamper
     * @param text
     * @return 匹配到的文本内容,对应的x轴,对应的y轴
     */
    public static List<Map<String,String>> findAppendText(PdfStamper stamper,int pageIndex, String text){
        PdfReader pdfReader = stamper.getReader();
        PdfReaderContentParser parser = new PdfReaderContentParser(pdfReader);
        List<Map<String,String>> resultList = new ArrayList();
        RenderListener listener = new RenderListener(){

            @Override
            public void beginTextBlock() {}

            @Override
            public void renderText(TextRenderInfo textRenderInfo) {
                String context = textRenderInfo.getText();
                if(context.contains(text)){
                    Map<String,String> result = new HashMap();
                    result.put("MatchText",context);
                    Rectangle2D.Float textRectangle = textRenderInfo.getDescentLine().getBoundingRectange();
                    result.put("x",String.valueOf(textRectangle.getX()));
                    result.put("y",String.valueOf(textRectangle.getY()));
                    resultList.add(result);
                }
            }

            @Override
            public void endTextBlock() {}

            @Override
            public void renderImage(ImageRenderInfo imageRenderInfo) {}
        };
        try {
            parser.processContent(pageIndex, listener);
        }catch(Exception e){
            log.error("查找特征值异常:{}",e.getMessage(),e);
        }
        return resultList;
    }

    public static void main(String[] args) throws Exception {
        FileInputStream fis = new FileInputStream("C:\\Users\\64853\\Desktop\\test.pdf");
        FileOutputStream fos = new FileOutputStream("C:\\Users\\64853\\Desktop\\test2.pdf");
        PdfStamper pdfStamper = createPdfStamper(fis, fos);
        List<Map<String, String>> names = findAppendText(pdfStamper, 1,"姓名");
        Map<String,String> name = names.get(0);
        appendText(pdfStamper,1,Float.valueOf(name.get("x"))+100,Float.valueOf(name.get("y")),10.56f,"朱佳叶");
        closeReader(pdfStamper);
    }
}
