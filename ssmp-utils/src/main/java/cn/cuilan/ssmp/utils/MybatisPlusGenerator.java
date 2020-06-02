package cn.cuilan.ssmp.utils;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.builder.ConfigBuilder;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;

/**
 * Mybatis 代码生成器
 *
 * @author zhang.yan
 */
public class MybatisPlusGenerator {

    public static void main(String[] args) {

        // 使用人员常规使用需要关心的参数
        String[] tableNames = new String[]{"t_user"};

        // 实体包名
        String entityPackageName = "cn.cuilan.ssmp.entity";

        // mapper包名
        String mapperPackageName = "cn.cuilan.ssmp.mapper";

        // 是否覆盖已存在的文件
        boolean fileOverride = false;

        MybatisPlusGenerator mybatisPlusGenerator = new MybatisPlusGenerator();
        mybatisPlusGenerator.gen(tableNames, entityPackageName, mapperPackageName, fileOverride);
    }

    /**
     * 固有配置-根据项目约定
     */
    public void gen(String[] tableNames, String entityPackageName, String mapperPackageName, boolean fileOverride) {

        //配置数据源
        DataSourceConfig dataSourceConfig = new DataSourceConfig();
        dataSourceConfig.setUrl("jdbc:mysql://localhost:3306/zy_test?characterEncoding=UTF-8");
        dataSourceConfig.setDriverName("com.mysql.jdbc.Driver");
        dataSourceConfig.setUsername("root");
        dataSourceConfig.setPassword("123456");

        // 配置全局
        GlobalConfig globalConfig = new GlobalConfig();
        // MacOS or Linux
        // String projectPath = System.getProperty("user.dir") + "/ssmp-frameword/";
        // Windows
        String projectPath = "D:/code/ssmp-frameword/";
        // 最终生成位置 = outPutDir + packageName
        globalConfig.setOutputDir(projectPath + "/src/main/java");
        globalConfig.setFileOverride(fileOverride);
        System.out.println("fileOverride config：" + fileOverride);
        globalConfig.setOpen(false);
        globalConfig.setIdType(IdType.AUTO);

        // 配置模板，配置取消生成以下类及文件
        TemplateConfig templateConfig = new TemplateConfig();
        // 取消生成 *Controller
        templateConfig.setController(null);
        // 取消生成 *ServiceImpl
        templateConfig.setServiceImpl(null);
        // 取消生成 *Service
        templateConfig.setService(null);
        // 取消生成 *Mapper.xml
        templateConfig.setXml(null);

        // 配置包
        PackageConfig packageConfig = new PackageConfig();
        packageConfig.setParent(null);
        packageConfig.setEntity(entityPackageName);
        packageConfig.setMapper(mapperPackageName);
        packageConfig.setXml(mapperPackageName);

        // 配置生成策略
        StrategyConfig strategyConfig = new StrategyConfig();
        strategyConfig.setTablePrefix("t_");
        strategyConfig.setNaming(NamingStrategy.underline_to_camel);
        strategyConfig.setColumnNaming(NamingStrategy.underline_to_camel);
        // 只能采用全局生成 @TableFiled 注解的形式。
        // 3.1.2版本的bug,字段自动添加@TableField注解的逻辑有问题。https://github.com/baomidou/mybatis-plus/issues/1393
        strategyConfig.setEntityTableFieldAnnotationEnable(true);
        // 设置实体继承父类
        strategyConfig.setSuperEntityClass("cn.cuilan.ssmp.common.BaseIdTimeEntity");
        // 设置实体从父类继承的字段
        strategyConfig.setSuperEntityColumns("id");
        // 设置Mapper继承父类
        strategyConfig.setSuperMapperClass("cn.cuilan.ssmp..mapper.CommonMapper");
        // 设置实体是否打开 Lombok 插件
        strategyConfig.setEntityLombokModel(true);
        // 设置实体布尔类型参数是否删除 is 开头前缀
        strategyConfig.setEntityBooleanColumnRemoveIsPrefix(true);
        strategyConfig.setInclude(tableNames);
        // 当需要 例如：t_mobile 生成实体 Phone、PhoneMapper 的时候用于转化
//        strategyConfig.setNameConvert(new INameConvert() {
//            @Override
//            public String entityNameConvert(TableInfo tableInfo) {
//                return null;
//            }
//            @Override
//            public String propertyNameConvert(TableField field) {
//                return null;
//            }
//        });

        ConfigBuilder config = new ConfigBuilder(packageConfig, dataSourceConfig, strategyConfig, templateConfig, globalConfig);
        // 3.1.2版本的bug。必须设置一个对象。https://github.com/baomidou/mybatis-plus/issues/1392
        config.setInjectionConfig(new InjectionConfig() {
            @Override
            public void initMap() {
            }
        });

        // 使得生成器不生成xml文件
        config.getPathInfo().remove(ConstVal.XML_PATH);
        String xmlPath = projectPath + "/src/main/resources/mybatis";
        config.getPathInfo().put(ConstVal.XML_PATH, xmlPath);

        // 代码生成
        AutoGenerator autoGenerator = new AutoGenerator();
        autoGenerator.setConfig(config);
        autoGenerator.execute();

        System.out.println("生成完成");
    }
}
