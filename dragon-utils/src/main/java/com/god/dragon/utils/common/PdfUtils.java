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
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.util.Base64Utils;


import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * PDFBOX会报一个C:\WINDOWS\FONTS\mstmc.ttf无法加载
 * 这个是WINDOWS10系统的一个BUG字体,不影响代码执行,如需修改则需要该底包
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

    /**
     * PDF转图片
     * @param in PDF文件输入流
     * @param out 图片文件输出流
     * @param imgSuffix 图片后缀 png jpg...
     */
    public static void pdf2Img(InputStream in,OutputStream out,String imgSuffix){
        try(PDDocument document = PDDocument.load(in)){
            PDFRenderer pdfRenderer = new PDFRenderer(document);
            List<BufferedImage> bufferedImages = new ArrayList<>();
            for (int pageIndex = 0; pageIndex < document.getNumberOfPages(); pageIndex++) {
                //将pdf转成图片,像素点越大,转成的图片越精细,对应的开销也越大
                BufferedImage bim = pdfRenderer.renderImageWithDPI(pageIndex, 300);
                //图片拼接成一个图片
                bufferedImages.add(bim);
            }
            ImageIO.write(combineImg(bufferedImages), imgSuffix, out);
        }catch (Exception e){
            log.error("PDF转成图片失败:{}",e.getMessage(),e);
            throw new RuntimeException("PDF转成图片失败");
        }
    }

    /**
     * PDF转图片
     * @param in PDF文件输入流
     * @param imgSuffix 图片后缀 png jpg...
     * @return 返回生成后图片的字节数组
     */
    public static byte[] pdf2Img(byte[] in,String imgSuffix){
        try(PDDocument document = PDDocument.load(in);
            ByteArrayOutputStream bos = new ByteArrayOutputStream()){
            PDFRenderer pdfRenderer = new PDFRenderer(document);
            List<BufferedImage> bufferedImages = new ArrayList<>();
            for (int pageIndex = 0; pageIndex < document.getNumberOfPages(); pageIndex++) {
                //将pdf转成图片,像素点越大,转成的图片越精细,对应的开销也越大
                BufferedImage bim = pdfRenderer.renderImageWithDPI(pageIndex, 300);
                //图片拼接成一个图片
                bufferedImages.add(bim);
            }
            ImageIO.write(combineImg(bufferedImages), imgSuffix, bos);
            return bos.toByteArray();
        }catch (Exception e){
            log.error("PDF转成图片失败:{}",e.getMessage(),e);
            throw new RuntimeException("PDF转成图片失败");
        }
    }

    /**
     * PDF转图片
     * @param in PDF文件输入流
     * @param imgSuffix 图片后缀 png jpg...
     * @return 不合并图片返回每个图片的Base64
     */
    public static List<String> pdf2ImgWithoutCombine(byte[] in,String imgSuffix){
        try(PDDocument document = PDDocument.load(in);
            ByteArrayOutputStream bos = new ByteArrayOutputStream(1024*1024)){
            PDFRenderer pdfRenderer = new PDFRenderer(document);
            List<String> bufferedImages = new ArrayList<>();
            for (int pageIndex = 0; pageIndex < document.getNumberOfPages(); pageIndex++) {
                //将pdf转成图片,像素点越大,转成的图片越精细,对应的开销也越大
                BufferedImage bim = pdfRenderer.renderImageWithDPI(pageIndex, 300);
                ImageIO.write(bim, imgSuffix, bos);
                //将图片转成Base64
                String base64 = Base64Utils.encodeToString(bos.toByteArray());
                bufferedImages.add(base64);
                bos.reset();
            }
            return bufferedImages;
        }catch (Exception e){
            log.error("PDF转成图片失败:{}",e.getMessage(),e);
            throw new RuntimeException("PDF转成图片失败");
        }
    }

    /**
     * 图片的上下拼接
     * @param list
     * @return
     */
    private static BufferedImage combineImg(List<BufferedImage> list){
        int maxWidth = 0,totalHeight = 0;
        //获取拼接后图片最大的宽度，获取拼接后图片总高度
        for (BufferedImage bufferedImage : list) {
            if(bufferedImage.getWidth()>maxWidth){
                maxWidth = bufferedImage.getWidth();
            }
            totalHeight += bufferedImage.getHeight();
        }
        //根据拼接后图片最大的宽度和拼接后图片总高度生成画布
        BufferedImage combinedImage = new BufferedImage(maxWidth, totalHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = combinedImage.createGraphics();
        int drawWidth = 0,drawHeight = 0;
        //遍历拼接画图
        for (BufferedImage bufferedImage : list) {
            g2d.drawImage(bufferedImage, drawWidth, drawHeight, null);
            drawHeight +=bufferedImage.getHeight();
        }
        g2d.dispose(); //关闭画笔资源
        return combinedImage;
    }

    public static void main(String[] args) throws Exception {
        FileInputStream fis = new FileInputStream("C:\\Users\\64853\\Desktop\\test.pdf");
        FileOutputStream fos = new FileOutputStream("C:\\Users\\64853\\Desktop\\test2.pdf");
//        PdfStamper pdfStamper = createPdfStamper(fis, fos);
//        List<Map<String, String>> names = findAppendText(pdfStamper, 1,"姓名");
//        Map<String,String> name = names.get(0);
//        appendText(pdfStamper,1,Float.valueOf(name.get("x"))+100,Float.valueOf(name.get("y")),10.56f,"朱佳叶");
//        closeReader(pdfStamper);

        fos = new FileOutputStream("C:\\Users\\64853\\Desktop\\test2.png");
        pdf2Img(fis,fos,"png");
    }
}
