create database familysystem;

create table familysystem.video_category(
    category_id int primary key auto_increment,
    category_name varchar(10),
    create_time datetime default now(),
    update_time datetime default now()
) comment '视频类别';

create table familysystem.video_info(
    info_id int primary key auto_increment,
    video_name varchar(50),
    video_type varchar(5) comment '视频形式： 单独、合集',
    order_no int comment '排序号',
    category_id int comment '外键，类别ID',
    create_time datetime default now(),
    update_time datetime default now()
) comment '视频信息';
alter table video_info add column(relative_path varchar(50) comment '相对路径');

create table familysystem.video_physics_info(
    video_id int primary key auto_increment,
    video_name varchar(100),
    info_id int comment '外键，视频信息ID',
    quarter_info varchar(5) default '无' comment '季度（第一季等）',
    order_no int comment '排序号',
    create_time datetime default now(),
    update_time datetime default now()
) comment '视频物理信息';
alter table video_physics_info add column(relative_path varchar(150) comment '相对路径');
alter table video_physics_info add column(video_long_time int default 0 comment '视频时长');
alter table video_physics_info add column(video_long_time_vis varchar(10) comment '视频时长可视化');

insert into familysystem.video_category(category_name) values('MV'),('电视剧'),('动漫'),('纪录片'),('技能'),('电影'),('其他');

CREATE TABLE familysystem.video_play_history (
    history_id int primary key AUTO_INCREMENT,
    video_id int not null comment '外键，视频ID',
    account_name varchar(10) not null comment '账户名称',
    create_time datetime default now(),
    update_time datetime default now()
)  COMMENT='视频播放历史';
alter table video_play_history add column(play_time long not null comment '播放时长 单位秒');
alter table video_play_history add column(play_time_vis varchar(10) comment '可视化播放时长 00:00:00');