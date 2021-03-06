-- common tables

INSERT INTO organizations (id, name, version) VALUES
  (1, '大内商事', 1),
  (2, '福本デザイン', 1),
  (3, '株式会社入江', 1),
  (4, '松沢セメント', 1);


INSERT INTO users (id, name) VALUES
  (10001, '武田 裕司'),
  (10002, '沼田 武蔵'),
  (10003, '内海 雪生'),
  (10004, '緒方 妃加'),
  (10005, '伊藤 悠葵'),
  (10006, '原口 夏羽'),
  (10007, '長沢 武雄'),
  (10008, '北村 太河'),
  (10009, '福原 優魅'),
  (10010, '石塚 雅弓'),
  (10011, '内田 真吾'),
  (10012, '北原 嵯央'),
  (10013, '天野 薫'),
  (10014, '坂口 架児'),
  (10015, '石塚 博幸'),
  (10016, '山岸 祐'),
  (10017, '吉川 美薫'),
  (10018, '秋元 美亜子'),
  (10019, '大村 友昭'),
  (10020, '村上 茉似'),
  (10021, '大塚 亮介'),
  (10022, '高山 梨歩'),
  (10023, '大城 翔也'),
  (10024, '長尾 泉岐'),
  (10025, '山田 紀衣'),
  (10026, '藤田 広太郎'),
  (10027, '福永 瑠奈'),
  (10028, '池田 海希'),
  (10029, '秋山 緋音'),
  (10030, '松崎 文実');


INSERT INTO employees(organization_id, user_id, version) VALUES
  (1, 10001, 1),
  (1, 10002, 1),
  (1, 10003, 1),
  (1, 10004, 1),
  (1, 10005, 1),
  (1, 10006, 1),
  (1, 10007, 1),
  (1, 10008, 1),
  (1, 10009, 1),
  (1, 10010, 1),
  (1, 10011, 1),
  (1, 10012, 1),
  (1, 10013, 1),
  (1, 10014, 1),
  (1, 10015, 1),
  (1, 10016, 1),
  (1, 10017, 1),
  (1, 10018, 1),
  (1, 10019, 1),
  (1, 10020, 1),
  (1, 10021, 1),
  (1, 10022, 1),
  (1, 10023, 1),
  (1, 10024, 1),
  (1, 10025, 1),
  (1, 10026, 1),
  (1, 10027, 1),
  (1, 10028, 1),
  (1, 10029, 1),
  (1, 10030, 1);

INSERT INTO path_enum_dept(id, organization_id, name, path, display_order) VALUES
  (100, 1, '大内商事', '100', 1),
  (101, 1, '株主総会', '100/101', 1),
  (102, 1, '取締役会', '100/102', 1),
  (103, 1, '社長', '100/102/103', 1),
  (104, 1, '総務部', '100/102/103/104', 1),
  (105, 1, '品質保証部', '100/102/103/105', 2),
  (106, 1, '環境課', '100/102/103/105/106', 1),
  (107, 1, '営業部', '100/102/103/107', 3),
  (108, 1, '製品開発室', '100/102/103/108', 4),
  (109, 1, '法人営業課', '100/102/103/107/109', 2),
  (110, 1, '東日本担当', '100/102/103/107/109/110', 1),
  (111, 1, '西日本担当', '100/102/103/107/109/111', 2),
  (112, 1, 'アジア担当', '100/102/103/107/109/112', 3),
  (113, 1, '個人営業課', '100/102/103/107/113', 1);

INSERT INTO path_enum_dept_user(organization_id, user_id, dept_id) VALUES
  (1, 10021, 101),
  (1, 10022, 101),
  (1, 10009, 102),
  (1, 10012, 102),
  (1, 10019, 102),
  (1, 10001, 103),
  (1, 10002, 104),
  (1, 10011, 104),
  (1, 10014, 104),
  (1, 10006, 105),
  (1, 10013, 105),
  (1, 10017, 106),
  (1, 10023, 106),
  (1, 10028, 107),
  (1, 10030, 107),
  (1, 10003, 108),
  (1, 10007, 108),
  (1, 10016, 108),
  (1, 10018, 108),
  (1, 10008, 109),
  (1, 10026, 109),
  (1, 10027, 109),
  (1, 10004, 110),
  (1, 10005, 110),
  (1, 10015, 111),
  (1, 10024, 111),
  (1, 10010, 112),
  (1, 10020, 113),
  (1, 10025, 113),
  (1, 10029, 113);

INSERT INTO nested_set_dept(id, organization_id, name, left_index, right_index, display_order) VALUES
  (100, 1, '大内商事', 0, 27, 1),
  (101, 1, '株主総会', 1, 2, 1),
  (102, 1, '取締役会', 3, 26, 1),
  (103, 1, '社長', 4, 25, 1),
  (104, 1, '総務部', 5, 6, 1),
  (105, 1, '品質保証部', 7, 10, 2),
  (106, 1, '環境課', 8, 9, 1),
  (107, 1, '営業部', 13, 24, 3),
  (108, 1, '製品開発室', 11, 12, 4),
  (109, 1, '法人営業課', 14, 21, 2),
  (110, 1, '東日本担当', 15, 16, 1),
  (111, 1, '西日本担当', 17, 18, 2),
  (112, 1, 'アジア担当', 19, 20, 3),
  (113, 1, '個人営業課', 22, 23, 1);

INSERT INTO nested_set_dept_user(organization_id, user_id, dept_id) VALUES
  (1, 10021, 101),
  (1, 10022, 101),
  (1, 10009, 102),
  (1, 10012, 102),
  (1, 10019, 102),
  (1, 10001, 103),
  (1, 10002, 104),
  (1, 10011, 104),
  (1, 10014, 104),
  (1, 10006, 105),
  (1, 10013, 105),
  (1, 10017, 106),
  (1, 10023, 106),
  (1, 10028, 107),
  (1, 10030, 107),
  (1, 10003, 108),
  (1, 10007, 108),
  (1, 10016, 108),
  (1, 10018, 108),
  (1, 10008, 109),
  (1, 10026, 109),
  (1, 10027, 109),
  (1, 10004, 110),
  (1, 10005, 110),
  (1, 10015, 111),
  (1, 10024, 111),
  (1, 10010, 112),
  (1, 10020, 113),
  (1, 10025, 113),
  (1, 10029, 113);

INSERT INTO dept_names(id, organization_id, name, display_order) VALUES
  (100, 1, '大内商事', 1),
  (101, 1, '株主総会', 1),
  (102, 1, '取締役会', 1),
  (103, 1, '社長', 1),
  (104, 1, '総務部', 1),
  (105, 1, '品質保証部', 2),
  (106, 1, '環境課', 1),
  (107, 1, '営業部', 3),
  (108, 1, '製品開発室', 4),
  (109, 1, '法人営業課', 2),
  (110, 1, '東日本担当', 1),
  (111, 1, '西日本担当', 2),
  (112, 1, 'アジア担当', 3),
  (113, 1, '個人営業課', 1);

INSERT INTO closure_table_dept(parent_id, child_id) VALUES
  (100, 100),
  (101, 100),
  (101, 101),
  (102, 100),
  (102, 102),
  (103, 100),
  (103, 102),
  (103, 103),
  (104, 100),
  (104, 102),
  (104, 103),
  (104, 104),
  (105, 100),
  (105, 102),
  (105, 103),
  (105, 105),
  (106, 100),
  (106, 102),
  (106, 103),
  (106, 105),
  (106, 106),
  (107, 100),
  (107, 102),
  (107, 103),
  (107, 107),
  (108, 100),
  (108, 102),
  (108, 103),
  (108, 108),
  (109, 100),
  (109, 102),
  (109, 103),
  (109, 107),
  (109, 109),
  (110, 100),
  (110, 102),
  (110, 103),
  (110, 107),
  (110, 109),
  (110, 110),
  (111, 100),
  (111, 102),
  (111, 103),
  (111, 107),
  (111, 109),
  (111, 111),
  (112, 100),
  (112, 102),
  (112, 103),
  (112, 107),
  (112, 109),
  (112, 112),
  (113, 100),
  (113, 102),
  (113, 103),
  (113, 107),
  (113, 113);

INSERT INTO closure_table_dept_user(organization_id, user_id, dept_id) VALUES
  (1, 10021, 101),
  (1, 10022, 101),
  (1, 10009, 102),
  (1, 10012, 102),
  (1, 10019, 102),
  (1, 10001, 103),
  (1, 10002, 104),
  (1, 10011, 104),
  (1, 10014, 104),
  (1, 10006, 105),
  (1, 10013, 105),
  (1, 10017, 106),
  (1, 10023, 106),
  (1, 10028, 107),
  (1, 10030, 107),
  (1, 10003, 108),
  (1, 10007, 108),
  (1, 10016, 108),
  (1, 10018, 108),
  (1, 10008, 109),
  (1, 10026, 109),
  (1, 10027, 109),
  (1, 10004, 110),
  (1, 10005, 110),
  (1, 10015, 111),
  (1, 10024, 111),
  (1, 10010, 112),
  (1, 10020, 113),
  (1, 10025, 113),
  (1, 10029, 113);
