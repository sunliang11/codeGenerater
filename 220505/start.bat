java -jar ufast-gen-code-1.1.0.jar --server.port=8082 ^
--dbType=mysql --spring.datasource.driverClassName=com.mysql.jdbc.Driver ^
--spring.datasource.url=jdbc:mysql://172.172.178.159:3307/chaos_platform?characterEncoding=utf8&autoReconnect=true&autoReconnectForPools=true&useUnicode=true&allowMultiQueries=true&serverTimezone=UTC&useSSL=false ^
--spring.datasource.username=chaos_user --spring.datasource.password=chaos_user ^
--packageName=com.haiziwang.chaos --author=auto_user --email= --tablePrefix=t_ --templatePackage=haiziwang/perm  &

