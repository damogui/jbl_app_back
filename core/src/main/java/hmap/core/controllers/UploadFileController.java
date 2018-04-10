package hmap.core.controllers;

import com.codahale.metrics.annotation.Timed;
import com.hand.hap.system.controllers.BaseController;
import hmap.core.hms.uploader.dto.UploadFileDTO;
import hmap.core.hms.uploader.service.IHmsUploadObjectService;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (c) 2016. Hand Enterprise Solution Company. All right reserved.
 * Project Name:HjingboParent
 * Package Name:hjingbo.core.controllers
 * Date:2016/9/8
 * Create By:jiguang.sun@hand-china.com
 */
@Controller
public class UploadFileController extends BaseController {

    @Autowired
    IHmsUploadObjectService hmsUploadObjectService;
    private Logger logger = LoggerFactory.getLogger(UploadFileController.class);


    @ResponseBody
    @RequestMapping(value = "/api/uploadFile", method = RequestMethod.POST)
    @Timed
    public JSONObject uploadImgWithoutThumbnail(@RequestParam(value = "file") MultipartFile file) {

        logger.info("files    :{} ", file.getSize());
        UploadFileDTO upd = null;
        JSONObject jsonObject = new JSONObject();

        //多张图片上传
        /*StringBuffer fileUrl = new StringBuffer("");
        StringBuffer fileName = new StringBuffer("");

        List<UploadFileDTO> list = new ArrayList<>();
        for (MultipartFile file : files) {
            try {
                upd = hmsUploadObjectService.uploadFile(file, false);
                list.add(upd);
            } catch (IOException e) {
                logger.error("文件上传错误:" + e.getMessage());
                e.printStackTrace();
                list = null;
            }
        }

        if (list == null || list.isEmpty()) {
            jsonObject.put("result", "E");
            jsonObject.put("message", "上传失败");
            logger.info("return result:{}", jsonObject);
            return jsonObject;
        }

        logger.info("list ;{}", list.size());
        jsonObject.put("result", "S");
        jsonObject.put("message", "上传成功");

        for (UploadFileDTO uploadFileDTO : list) {
            int index = uploadFileDTO.getObjectUrl().lastIndexOf("/");
            fileName.append(uploadFileDTO.getObjectUrl().substring(index + 1)).append(",");
            fileUrl.append(uploadFileDTO.getObjectUrl()).append(",");
        }
        String fileNames = fileName.substring(0, fileName.length() - 1);
        String fileUrls = fileUrl.substring(0, fileUrl.length() - 1);

        jsonObject.put("fileName", fileNames);
        jsonObject.put("url", fileUrls);*/


//        单张图片上传
        try {
            upd = hmsUploadObjectService.uploadFile(file, false);

        } catch (IOException e) {
            logger.error("文件上传错误:" + e.getMessage());
            e.printStackTrace();
        }

        if (upd == null) {
            jsonObject.put("result", "E");
            jsonObject.put("message", "上传失败");
            logger.info("return result:{}", jsonObject);
            return jsonObject;
        }

        jsonObject.put("result", "S");
        jsonObject.put("message", "上传成功");
        int index = upd.getObjectUrl().lastIndexOf("/");
        logger.info("return  url={}",upd.getObjectUrl());
        String fileName = upd.getObjectUrl().substring(index + 1);
        String  fileUrl = upd.getObjectUrl();

        jsonObject.put("fileName", fileName);
        jsonObject.put("url", fileUrl);



        logger.info("return result :{}", jsonObject);
        return jsonObject;
    }


}
