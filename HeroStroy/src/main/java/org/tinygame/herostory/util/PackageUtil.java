package org.tinygame.herostory.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

/**
 * 名称空间使用工具类
 *
 * @author QuLei
 */
public final class PackageUtil {
    /**
     * 私有化构造方法
     */
    private PackageUtil() {
    }

    /**
     * 列取指定包中的所有子类
     *
     * @param packageName 包名
     * @param recursive   是否递归查找
     * @param superClazz  父类的类型
     * @return 子类集合
     */
    public static Set<Class<?>> listSubClazz(
            String packageName,
            boolean recursive,
            Class<?> superClazz
    ) {
        if (null == superClazz) {
            return Collections.emptySet();
        } else {
            return listClazz(packageName, recursive, superClazz::isAssignableFrom);
        }
    }

    public static Set<Class<?>> listClazz(
            String packageName, boolean recursive, IClazzFilter filter) {
        if (null == packageName || packageName.isEmpty()) {
            return null;
        }
        //将包名的.转化为"/"
        final String packagePath = packageName.replace('.', '/');
        //获取类加载器
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        //结果集合
        Set<Class<?>> resultSet = new HashSet<>();
        try {
            //获取URL枚举
            Enumeration<URL> urlEnum = cl.getResources(packagePath);
            while (urlEnum.hasMoreElements()) {
                //获取当前URL
                URL currUrl = urlEnum.nextElement();
                //获取协议文本
                final String protocol = currUrl.getProtocol();
                //定义临时集合
                Set<Class<?>> temSet = null;
                if ("FILE".equalsIgnoreCase(protocol)) {
                    //从文件系统中加载类
                    temSet = listClazzFromDir(
                            new File(currUrl.getFile()), packageName, recursive, filter
                    );
                } else if ("JAR".equalsIgnoreCase(protocol)) {
                    //获取文件字符串
                    String fileStr = currUrl.getFile();
                    if (fileStr.startsWith("file:")) {
                        //如果是以"file:"开头的，则去除开头
                        fileStr = fileStr.substring(5);
                    }
                    if (fileStr.lastIndexOf('!') > 0) {
                        //如果有'!'字符，则截取'!'字符后面所有的字符
                        fileStr = fileStr.substring(0, fileStr.lastIndexOf('!'));
                    }
                    //从JAR文件中加载类
                    temSet = listClassFromJar(new File(fileStr), packageName, recursive, filter);
                }
                if (temSet != null) {
                    resultSet.addAll(temSet);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return resultSet;
    }


    /**
     * 从目录中获取类列表
     *
     * @param dirFile     目录
     * @param packageName 包名
     * @param recursive   是否递归查询子包
     * @param filter      类过滤器
     * @return 符合条件的类集合
     */
    private static Set<Class<?>> listClazzFromDir(
            final File dirFile, final String packageName, final boolean recursive,
            IClazzFilter filter) {
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            //如果参数对象为空，则直接退出
            return null;
        }

        //获取子文件列表
        File[] subFileArr = dirFile.listFiles();
        if (subFileArr == null || subFileArr.length <= 0) {
            return null;
        }

        //文件队列，将子文件列表添加到队列
        Queue<File> fileQ = new LinkedList<>(Arrays.asList(subFileArr));
        //结果对象
        Set<Class<?>> resultSet = new HashSet<>();
        while (!fileQ.isEmpty()) {
            //从队列中获取文件
            File currFile = fileQ.poll();
            if (currFile.isDirectory() && recursive) {
                //如果当前文件是目录且支持递归操作时，获取子文件列表
                subFileArr = currFile.listFiles();
                if (subFileArr != null && subFileArr.length > 0) {
                    fileQ.addAll(Arrays.asList(subFileArr));
                }
                continue;
            }

            if (!currFile.isFile() || !currFile.getName().endsWith(".class")) {
                //如果当前文件不是文件，或者文件名不是以.class结尾，直接跳过
                continue;
            }
            //类名称
            String clazzName;
            //设置类名称
            clazzName = currFile.getAbsolutePath();
            //清除最后的.class结尾
            clazzName = clazzName.substring(dirFile.getAbsolutePath().length(), clazzName.lastIndexOf('.'));
            //转换目录斜杠
            clazzName = clazzName.replace('\\', '/');
            //清除开头的/
            clazzName = trimLeft(clazzName, "/");
            //将所有的  /  修改为.
            clazzName = join(clazzName.split("/"), ".");
            //包名 + 类名
            clazzName = packageName + "." + clazzName;

            try {
                Class<?> clazzObj = Class.forName(clazzName);
                if (null != filter && !filter.accept(clazzObj)) {
                    //如果过滤器不为空，且过滤器不接受当前类，直接跳过
                    continue;
                }

                //添加该类到集合
                resultSet.add(clazzObj);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        return resultSet;
    }

    /**
     * 从.jar文件中获取类列表
     *
     * @param jarFilePath .jar文件路径
     * @param packageName 包名
     * @param recursive   是否递归查询子包
     * @param filter      类过滤器
     * @return 符合条件的集合
     */
    private static Set<Class<?>> listClassFromJar(
            final File jarFilePath,
            String packageName,
            boolean recursive,
            IClazzFilter filter) {
        if (null == jarFilePath || jarFilePath.isDirectory()) {
            //如果参数对象为空，或者是目录直接退出
            return null;
        }
        Set<Class<?>> resultSet = new HashSet<>();
        try {
            JarInputStream jarIn = new JarInputStream(new FileInputStream(jarFilePath));
            //进入点
            JarEntry entry;
            while ((entry = jarIn.getNextJarEntry()) != null) {
                if (entry.isDirectory()) {
                    continue;
                }
                //获取进入点名称
                String entryName = entry.getName();
                if (!entryName.endsWith(".class")) {
                    //如果是以.class结尾，说明不是JAVA类文件，直接跳过
                    continue;
                }
                if (!recursive) {
                    //如果没有开启递归模式，那么就需要判断当前.class文件是否在指定目录下

                    //获取目录名称
                    String temStr = entryName.substring(0, entryName.lastIndexOf('/'));
                    //将目录中的“/”全部换成 “.”
                    temStr = join(temStr.split("/"), ".");
                    if (!packageName.equals(temStr)) {
                        //如果包名和目录名不相同，直接跳过
                        continue;
                    }
                }

                String clazzName;
                //清楚最后的.class结尾
                clazzName = entryName.substring(0, entryName.lastIndexOf('.'));
                //将所有的 / 修改成.
                clazzName = join(clazzName.split("/"), ".");
                //加载类定义
                Class<?> clazzObject = Class.forName(clazzName);
                if (null != filter && !filter.accept(clazzObject)) {
                    //如果过滤器不为空，且过滤器不接受当前类，直接跳过
                    continue;
                }
                resultSet.add(clazzObject);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultSet;

    }

    /**
     * 清除源字符串左边的字符串
     *
     * @param src     源字符串
     * @param trimStr 需要被清除的字符串
     * @return 清除后的字符串
     */
    private static String trimLeft(String src, String trimStr) {
        if (null == src || src.isEmpty()) {
            return "";
        }
        if (null == trimStr || trimStr.isEmpty()) {
            return src;
        }
        if (src.equals(trimStr)) {
            return "";
        }
        while (src.startsWith(trimStr)) {
            src = src.substring(trimStr.length());
        }
        return src;
    }

    /**
     * 使用连接符连接字符串数组
     *
     * @param strArr 字符串数组
     * @param conn   连接符
     * @return 连接后的字符串
     */
    static private String join(String[] strArr, String conn) {
        if (null == strArr ||
                strArr.length <= 0) {
            return "";
        }

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < strArr.length; i++) {
            if (i > 0) {
                // 添加连接符
                sb.append(conn);
            }

            // 添加字符串
            sb.append(strArr[i]);
        }

        return sb.toString();
    }


    @FunctionalInterface
    public interface IClazzFilter {
        /**
         * 是否接受当前类
         *
         * @param clazz 被筛选的类
         * @return 是否符合条件
         */
        boolean accept(Class<?> clazz);
    }

}
