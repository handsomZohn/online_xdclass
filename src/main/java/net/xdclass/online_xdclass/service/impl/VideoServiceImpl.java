package net.xdclass.online_xdclass.service.impl;

import net.xdclass.online_xdclass.config.CacheKeyManager;
import net.xdclass.online_xdclass.model.entity.Video;
import net.xdclass.online_xdclass.model.entity.VideoBanner;
import net.xdclass.online_xdclass.mapper.VideoMapper;
import net.xdclass.online_xdclass.service.VideoService;
import net.xdclass.online_xdclass.utils.BaseCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;


@Service
public class VideoServiceImpl implements VideoService {

    @Autowired
    private VideoMapper videoMapper;


    @Autowired
    private BaseCache baseCache;


    @Override
    public List<Video> listVideo() {


        try{

          Object cacheObj =  baseCache.getTenMinuteCache().get(CacheKeyManager.INDEX_VIDEL_LIST,()->{

                List<Video> videoList = videoMapper.listVideo();

                return videoList;

            });

          if(cacheObj instanceof List){
              List<Video> videoList = (List<Video>)cacheObj;
              return videoList;
          }

        }catch (Exception e){
            e.printStackTrace();
        }

        //可以返回兜底数据，业务系统降级-》SpringCloud专题课程
        return null;
    }



    @Override
    public List<VideoBanner> listBanner() {

        try{

            Object cacheObj =  baseCache.getTenMinuteCache().get(CacheKeyManager.INDEX_BANNER_KEY, ()->{

                List<VideoBanner> bannerList =  videoMapper.listVideoBanner();

                System.out.println("从数据库里面找轮播图列表");

                return bannerList;

            });

            if(cacheObj instanceof List){
                List<VideoBanner> bannerList = (List<VideoBanner>)cacheObj;
                return bannerList;
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }




    @Override
    public Video findDetailById(int videoId) {

        //单独构建一个缓存key，每个视频的key是不一样的
        String videoCacheKey = String.format(CacheKeyManager.VIDEO_DETAIL,videoId);

        try{

         Object cacheObject = baseCache.getOneHourCache().get( videoCacheKey, ()->{

                // 需要使用mybaits关联复杂查询
                Video video = videoMapper.findDetailById(videoId);

                return video;

            });

         if(cacheObject instanceof Video){

             Video video = (Video)cacheObject;
             return video;
         }

        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public List<VideoBanner> getBannerList() {
        /**
         * 返回数据库查询数据或缓存热点数据
         */
        try {
            Object o = baseCache.getTenMinuteCache().get(CacheKeyManager.INDEX_BANNER_KEY, () -> {
                List<VideoBanner> videoBanners = videoMapper.listVideoBanner();
                return videoBanners;
            });
            if (o instanceof List) {
                List<VideoBanner> videoBanners = (List<VideoBanner>)o;
                return videoBanners;
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        /**
         * 服务降级返回兜底数据
         */
        return new ArrayList<VideoBanner>(){{
            add(new VideoBanner(){{
                setCreateTime(new Date());
                setId(10);
                setImg("");
                setUrl("");
                setWeight(10);
            }});
            add(new VideoBanner(){{
                setCreateTime(new Date());
                setId(20);
                setImg("");
                setUrl("");
                setWeight(10);
            }});
            add(new VideoBanner(){{
                setCreateTime(new Date());
                setId(30);
                setImg("");
                setUrl("");
                setWeight(10);
            }});
        }};
    }
}
