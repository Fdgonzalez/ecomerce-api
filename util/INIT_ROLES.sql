INSERT INTO roles(id,name) VALUES (1,'ROLE_USER');
INSERT INTO roles(id,name) VALUES (2,'ROLE_ADMIN');
INSERT INTO users(user_id,email,last_name,name,password,username) VALUES(1,'admin@localhost.com','Adminez','Admin','$2a$10$ijMKw3s4K.kZwONivBG/PuKOiQPGjM0iAUvKq80JINSFxmCFe.AXy','admin');
INSERT INTO user_roles(user_id,role_id) VALUES(1,2);
INSERT INTO user_roles(user_id,role_id) VALUES(1,1);