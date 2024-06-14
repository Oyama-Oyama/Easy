# sdk

#### 介绍
> 提供  公用方法支持、 广告支持、 firebase功能支持
>> ***Core***: 管理模块<br>
>> ***base***: 公用方法支持<br>
>> ***adCore***: 广告支持<br>
>> ***firebase***: firebase功能支持<br>

#### 引用准备条件
> 1. 应用 sdk 广告部分时，必须保证Application id 和 PackageName 一致，否则由于获取前台Activity实例失败，广告大概率展示失败

#### bug
> 1. banner 大量展示调用同时触发时同步竞争问题
> 2. native 大量展示调用同时触发时同步竞争问题
> 3. banner 未增加设定展示位置
> 4. 是否参与价格比较未设置为动态配置
> 5. 加载逻辑动态化配置
> 6. 增加打点功能
> 7. 是否可以将firebase模块拆分，避免同时引入大量未使用内容


#### 版本迭代
> ***v1.0.4.1***
>> *2023/03/07*
>> 1. Vungle bug 修复

> ***v1.0.3.22***
>> *2023/02/17*
>> 1. 增加Vungle

> ***v1.0.3.21***
>> *2023/02/17*
>> 1. 广告回调无效bug

> ***v1.0.3.1***
>> *2023/02/09*
>> 1. 增加MMKV
>> 2. 增加跳转 GooglePlay AppStore

> ***v1.0.3***
>> *2023/02/09*
>> 1. 支持admob、unity平台
>> 2. 加入firebase  analytics、remote-config、messaging

    