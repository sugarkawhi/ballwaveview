# ballwaveview
球状波浪进度指示器


### 演示

![](http://olpu32iyy.bkt.clouddn.com/17-9-13/57441031.jpg)

### TODO

未做的功能 也比较简单暂时先不考虑了
 
 + 不支持自定义波浪颜色和球颜色
 + 未添加进度文字
 
 ### 思路
 
+ 代码比较简单，主要是用来Path的二阶贝塞尔曲线，quadTo();总共5个点和4个控制点
+ 利用 PorterDuffXfermode 的PorterDuff.Mode.SRC_IN来实现波浪在小球

```
PorterDuff.Mode.SRC_IN
```


