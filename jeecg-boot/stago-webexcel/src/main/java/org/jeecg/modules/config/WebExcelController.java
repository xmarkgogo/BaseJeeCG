package org.jeecg.modules.config;
import com.grapecity.documents.excel.Workbook;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.Base64;


@RestController
@RequestMapping("/webexcel")
@Slf4j
public class WebExcelController {
    @RequestMapping(value = {"GetFileOnlyXls", ""})
    public String  GetFileOnlyXls(@RequestParam(name = "fileName", defaultValue = "") String fileName,
                                  HttpServletRequest req, HttpServletResponse resp) throws Exception {
        String FilePath=fileName;
        //base64 编码中使用了加号（+），而 + 在 URL 传递时会被当成空格，因此造成了base64字符串被更改，在服务器端解码后就会出错。
        FilePath = FilePath.replaceAll(" +","+");
        FilePath= new String(Base64.getDecoder().decode(FilePath.getBytes("UTF-8")));
        if(FilePath.lastIndexOf(".xlsx")>0){
            File file=new File(FilePath);
            if(file.exists()){
                Workbook workbook = new Workbook();
                String source = FilePath;
                workbook.open(source);
                ServletOutputStream servletOS = resp.getOutputStream(); //输出流
                workbook.save(servletOS);
            }
        }
        return null;
    }
}
