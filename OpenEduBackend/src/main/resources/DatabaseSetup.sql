create database OpenEdu default character set utf8 collate utf8_general_ci;
grant all privileges on OpenEdu.* to 'open_edu'@'%' identified by 'fifth_street' with grant option;