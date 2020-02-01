# Segment

[Segment](https://github.com/houbb/segment) 是基于结巴分词词库实现的更加灵活，高性能的 java 分词实现。

愿景：成为 java 最好用的分词工具。

[![Build Status](https://travis-ci.com/houbb/segment.svg?branch=master)](https://travis-ci.com/houbb/segment)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.houbb/segment/badge.svg)](http://mvnrepository.com/artifact/com.github.houbb/segment)
[![](https://img.shields.io/badge/license-Apache2-FF0080.svg)](https://github.com/houbb/segment/blob/master/LICENSE.txt)
[![Open Source Love](https://badges.frapsoft.com/os/v2/open-source.svg?v=103)](https://github.com/houbb/segment)

> [变更日志](https://github.com/houbb/segment/blob/master/CHANGELOG.md)

## 创作目的

分词是做 NLP 相关工作，非常基础的一项功能。

[jieba-analysis](https://github.com/huaban/jieba-analysis) 作为一款非常受欢迎的分词实现，个人实现的 [opencc4j](https://github.com/houbb/opencc4j) 之前一直使用其作为分词。

但是随着对分词的了解，发现结巴分词对于一些配置上不够灵活。

有很多功能无法指定关闭，比如 HMM 对于繁简体转换是无用的，因为繁体词是固定的，不需要预测。

最新版本的词性等功能好像也被移除了，但是这些都是个人非常需要的。

所以自己重新实现了一遍，希望实现一套更加灵活，更多特性的分词框架。

而且 jieba-analysis 的更新似乎停滞了，个人的实现方式差异较大，所以建立了全新的项目。

## Features 特点

- 面向用户的极简静态 api 设计

- 面向开发者 fluent-api 设计，让配置更加优雅灵活

- 详细的中文代码注释，便于源码阅读

- 基于 DFA 实现的高性能分词

- 允许指定自定义词库

- 支持不同的分词模式

- 支持全角半角/英文大小写格式处理

### 最新变更

- 支持 HMM 新词预测

- 优化内存占用

# 快速入门

## 准备

jdk1.7+

maven 3.x+

## maven 引入

```xml
<dependency>
    <groupId>com.github.houbb</groupId>
    <artifactId>segment</artifactId>
    <version>0.1.0</version>
</dependency>
```

相关代码参见 [SegmentHelperTest.java](https://github.com/houbb/segment/blob/master/src/test/java/com/github/houbb/segment/test/util/SegmentHelperTest.java)

## 默认分词示例

返回分词，下标等信息。

```java
final String string = "这是一个伸手不见五指的黑夜。我叫孙悟空，我爱北京，我爱学习。";

List<ISegmentResult> resultList = SegmentHelper.segment(string);
Assert.assertEquals("[这是[0,2), 一个[2,4), 伸手不见五指[4,10), 的[10,11), 黑夜[11,13), 。[13,14), 我[14,15), 叫[15,16), 孙悟空[16,19), ，[19,20), 我爱[20,22), 北京[22,24), ，[24,25), 我爱[25,27), 学习[27,29), 。[29,30)]", resultList.toString());
```

## 指定返回形式

有时候我们根据自己的应用场景，需要选择不同的返回形式。

`SegmentResultHandlers` 用来指定对于分词结果的处理实现，便于保证 api 的统一性。

| 方法 | 实现 | 说明 |
|:---|:---|:---|
| `common()` | SegmentResultHandler | 默认实现，返回 ISegmentResult 列表 |
| `word()` | SegmentResultWordHandler | 只返回分词字符串列表 |

### 默认模式

默认分词形式，等价于下面的写法

```java
List<ISegmentResult> resultList = SegmentHelper.segment(string, SegmentResultHandlers.common());
```

### 只获取分词信息

```java
final String string = "这是一个伸手不见五指的黑夜。我叫孙悟空，我爱北京，我爱学习。";

List<String> resultList = SegmentHelper.segment(string, SegmentResultHandlers.word());
Assert.assertEquals("[这是, 一个, 伸手不见五指, 的, 黑夜, 。, 我, 叫, 孙悟空, ，, 我爱, 北京, ，, 我爱, 学习, 。]", resultList.toString());
```

# 分词模式

## 分词模式简介

分词模式可以通过类 `SegmentModes` 工具类获取。

| 序号 | 方法 | 准确度 | 性能 | 备注 |
|:---|:---|:---|:---|:---|
| 1 | search() | 高 | 一般 | 结巴分词的默认模式 |
| 2 | index() | 一般 | 高 | 尽可能多的返回词组信息，提高召回率 |
| 3 | greedyLength() | 一般 | 高 | 贪心最大长度匹配，对准确度要求不高时可采用。 |

## 使用方式

针对灵活的配置，引入了 `SegmentBs` 作为引导类，解决工具类方法配置参数过多的问题。

测试代码参见 [SegmentModeTest.java](https://github.com/houbb/segment/blob/master/src/test/java/com/github/houbb/segment/test/bs/SegmentBsModeTest.java)

## search 模式

`segmentMode()` 指定分词模式，不指定时默认就是 `SegmentModes.search()`。

```java
final String string = "这是一个伸手不见五指的黑夜。";

List<ISegmentResult> resultList = SegmentBs.newInstance()
       .segmentMode(SegmentModes.search())
       .segment(string);

Assert.assertEquals("[这是[0,2), 一个[2,4), 伸手不见五指[4,10), 的[10,11), 黑夜[11,13), 。[13,14)]", resultList.toString());
```

## Index 模式

这里主要的区别就是会返回 `伸手`、`伸手不见` 等其他词组。

```java
final String string = "这是一个伸手不见五指的黑夜。";

List<ISegmentResult> resultList = SegmentBs.newInstance()
        .segmentMode(SegmentModes.index())
        .segment(string);
Assert.assertEquals("[这[0,1), 是[1,2), 一个[2,4), 伸手[4,6), 伸手不见[4,8), 伸手不见五指[4,10), 的[10,11), 黑夜[11,13), 。[13,14)]", resultList.toString());
```

## GreedyLength 模式

这里使用贪心算法实现，准确率一般，性能较好。

```java
final String string = "这是一个伸手不见五指的黑夜。";

List<ISegmentResult> resultList = SegmentBs.newInstance()
        .segmentMode(SegmentModes.greedyLength())
        .segment(string);
Assert.assertEquals("[这[0,1), 是[1,2), 一个[2,4), 伸手不见五指[4,10), 的[10,11), 黑夜[11,13), 。[13,14)]", resultList.toString());
```

# 格式处理

## 全角半角+英文大小写

这里的 `Ｑ` 为全角大写，默认会被转换处理。

```java
String text = "阿Ｑ精神";
List<ISegmentResult> segmentResults = SegmentHelper.segment(text);

Assert.assertEquals("[阿Ｑ[0,2), 精神[2,4)]", segmentResults.toString());
```

# Benchmark 性能对比

## 性能对比

性能对比基于 jieba 1.0.2 版本，测试条件保持一致，保证二者都做好预热，然后统一处理。

验证下来，默认模式性能略优于 jieba 分词，贪心模式是其性能 3 倍左右。

备注：

（1）默认模式和结巴 Search 模式一致。

后期考虑 HMM 也可以配置是否开启，暂定为默认开启

（2）后期将引入多线程提升性能。

代码参见 [BenchmarkTest.java](https://github.com/houbb/segment/blob/master/src/test/java/com/github/houbb/segment/test/benchmark/BenchmarkTest.java)

## 性能对比图

相同长文本，循环 1W 次耗时。（Less is Better）

![benchmark](https://github.com/houbb/segment/blob/master/benchmark.png)

# 后期 Road-Map

## 核心特性

- 中文繁简体格式化

- CRF 算法实现

- N 元组算法实现

## 优化

- 多线程的支持，性能优化

- 双数组 DFA 实现，降低内存消耗

## 辅助特性

- 拓展自定义词库的特性

# 创作感谢

感谢 [jieba](https://github.com/fxsjy/jieba) 分词提供的词库，以及 [jieba-analysis](https://github.com/huaban/jieba-analysis) 的相关实现。