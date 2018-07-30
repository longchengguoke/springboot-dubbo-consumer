package com.dubbo.consumer.controller;

import com.dubbo.server.entity.UserInfo;
import com.dubbo.server.entity.UserInfoVo;
import com.dubbo.server.service.CheckUserService;
import com.dubbo.server.utils.Md5Utils;
import com.dubbo.server.utils.PathUtils;
import com.dubbo.server.utils.PointUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Mr.C
 * @Description  用于用户登录信息检测
 * @create 2018/7/26 10:02
 * Copyright: Copyright (c) 2018
 * Company:CWWT
 */
@Controller
public class UserLogin {


    @Resource
    private CheckUserService checkUserService;

    /**
     * 读取excel文件中的用户信息，进行md5加密,并调用dubbo接口进行验证用户是否可用，将验证结果回传写入excel中
     */
    @RequestMapping(value = "/checkUser" ,produces = "application/json;charset=utf-8")
    @ResponseBody
    private String  checkUser() throws IOException, NoSuchAlgorithmException {

        List<UserInfoVo> infoVoList=new ArrayList<>();

        //1、读取excel中的用户信息
        FileInputStream in = new FileInputStream( PathUtils.getResourcePath().concat("calfile/userINfo.xlsx"));
        XSSFWorkbook wb=new XSSFWorkbook(in);
        XSSFSheet sheet = wb.getSheetAt(0);

        int num=sheet.getLastRowNum();
        int lastCellNum=0;
        for(int i=1;i<=num;i++){

            UserInfo userInfo=new UserInfo();

            XSSFRow row = sheet.getRow(i);

            int index=0;
            userInfo.setId(PointUtils.delPoint(row.getCell(index)));
            userInfo.setUserId(PointUtils.delPoint(row.getCell(++index)));
            userInfo.setUserName(PointUtils.delPoint(row.getCell(++index)));
            userInfo.setPassword(PointUtils.delPoint(row.getCell(++index)));
            userInfo.setSex(PointUtils.delPoint(row.getCell(++index)));
            userInfo.setAge(Integer.valueOf(PointUtils.delPoint(row.getCell(++index))));
            userInfo.setPhoneNo(Long.valueOf(PointUtils.delPoint(row.getCell(++index))));

            //计算出最后一列的位置
            lastCellNum=index+1;

            //2、将用户信息批量进行加密
            UserInfoVo infoVo=new UserInfoVo();
            infoVo.setId(userInfo.getId());
            infoVo.setMd5UserInfo(Md5Utils.encodeByMd5(userInfo.toString()));

            infoVoList.add(infoVo);
        }
        //3、调用dubbo接口，将结果返回
        infoVoList=checkUserService.checkUser(infoVoList);
        Map map=new HashMap<>(infoVoList.size());
        for(UserInfoVo infoVo:infoVoList){
            map.put(infoVo.getId(),infoVo.getEnableUser());
        }

        //4、将返回结果重新写入对应的excel列表中
        XSSFRow row = sheet.getRow(0);
        row.createCell(lastCellNum).setCellValue("结果");
        for (int i=1;i<=num;i++){
            row = sheet.getRow(i);
            String id=PointUtils.delPoint(row.getCell(0));
            String value=String.valueOf(map.get(id));
            row.createCell(lastCellNum).setCellValue(value);
        }
        in.close();
        OutputStream out = new FileOutputStream( PathUtils.getResourcePath().concat("calfile/userINfo.xlsx"));
        wb.write(out);
        out.close();

        return "success";
    }

}
