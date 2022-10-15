package com.atguigu.gulimall.search.controller;

import com.atguigu.gulimall.commons.bean.Constant;
import com.atguigu.gulimall.commons.bean.Resp;
import com.atguigu.gulimall.commons.es.EsSkuVo;
import io.searchbox.client.JestClient;
import io.searchbox.core.Delete;
import io.searchbox.core.Index;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/es")
public class SpuToEsController {

    @Autowired
    JestClient jestClient;

    /**
     * 商品上架
     *
     * @return
     */
    @PostMapping("/spu/up")
    public Resp<Object> spuUp(@RequestBody List<EsSkuVo> vo) {

        for (EsSkuVo esSkuVo : vo) {
            Index index = new Index.Builder(esSkuVo)
                    .index(Constant.ES_SPU_INDEX)
                    .type(Constant.ES_SPU_TYPE)
                    .id(esSkuVo.getId().toString())
                    .build();

            try {
                jestClient.execute(index);
            } catch (Exception e) {

            }

        }
        return Resp.ok(null);
    }

    @PostMapping("/spu/down")
    public Resp<Object> spuDown(@RequestBody List<EsSkuVo> vo) {

        for (int i = 0; i < vo.size(); i++) {
            Delete delete = new Delete.Builder(vo.get(i).getId().toString())
                    .index(Constant.ES_SPU_INDEX)
                    .type(Constant.ES_SPU_TYPE)
                    .build();
            try {
                jestClient.execute(delete);
            } catch (Exception e) {

            }
        }
        return Resp.ok(null);
    }
}
