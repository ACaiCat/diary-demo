# 日记本

@west2-online Android作业，一个简洁的日记应用，使用和风天气 API 获取天气信息。

## 截图

![0fd09e75986d2945cacc70656b67e1e9_720](https://github.com/user-attachments/assets/2725a676-8471-4b35-836f-f3fff49df300)


![823d2b93bcd05314535867ff4d2d4cba](https://github.com/user-attachments/assets/e4606440-b728-478a-9cb8-36d5f74d4b34)


## 构建配置

构建前，在项目根目录的 `local.properties` 文件中添加以下内容：

```
QWEATHER_BASE_URL=https://your-host.qweatherapi.com
QWEATHER_API_KEY=your_api_key_here
```

- `QWEATHER_BASE_URL`：和风天气 API 的请求域名，根据订阅套餐不同而有所差异，可在开发者控制台查看。
- `QWEATHER_API_KEY`：你在和风天气开发者控制台申请的个人 API 密钥。
