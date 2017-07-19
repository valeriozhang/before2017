CREATE TYPE payType AS ENUM ('credit', 'debit', 'cash');
CREATE TYPE alcType AS ENUM('whiskey', 'rum', 'vodka', 'gin', 'tequila', 'beer');
CREATE TYPE subType AS ENUM('monthly', 'bimonthly', 'weekly', 'biweekly');

CREATE TABLE cs421g34.users
(
    uid INTEGER,
    name VARCHAR(64),
    login VARCHAR(64),
    password VARCHAR(64),
    shipAddress VARCHAR(128),
    billAddress VARCHAR(128),
    PRIMARY KEY(uid),
    CHECK (uid > 0)
);

CREATE TABLE cs421g34.barOwners
(
    uid INTEGER,
    PRIMARY KEY (uid),
    FOREIGN KEY (uid) REFERENCES users(uid)
);

CREATE TABLE cs421g34.regCustomers
(
    uid INTEGER,
    PRIMARY KEY (uid),
    FOREIGN KEY (uid) REFERENCES users(uid)
);

CREATE TABLE cs421g34.alcohols
(
    aid INTEGER,
    name VARCHAR(64),
    abv INTEGER,
    aType alcType,
    volume INTEGER,
    price DECIMAL(7,2),
    PRIMARY KEY (aid),
    CHECK (0 < abv AND abv < 100)
);

CREATE TABLE cs421g34.transaction
(
    tid INTEGER,
    uid INTEGER,
    aid INTEGER,
    quantity INTEGER,
    totalCost DECIMAL(7,2),
    PRIMARY KEY (tid),
    FOREIGN KEY (uid) REFERENCES users(uid),
    FOREIGN KEY (aid) REFERENCES alcohols(aid)
);

CREATE TABLE cs421g34.payment
(
    pid INTEGER,
    uid INTEGER,
    tid INTEGER,
    pDate DATE,
    pType payType,
    amt DECIMAL(7,2),
    PRIMARY KEY (pid),
    FOREIGN KEY (uid) REFERENCES users(uid),
    FOREIGN KEY (tid) REFERENCES transaction(tid)
);

CREATE TABLE cs421g34.subscription
(
    uid INTEGER,
    aid INTEGER,
    pid INTEGER,
    quantity INTEGER,
    sType subType,
    pType payType,
    payAmt DECIMAL(7,2),
    startDate DATE,
    PRIMARY KEY (uid, aid),
    FOREIGN KEY (uid) REFERENCES users(uid),
    FOREIGN KEY (aid) REFERENCES alcohols(aid),
    FOREIGN KEY (pid) REFERENCES payment(pid)
);

INSERT INTO cs421g34.users (uid,name,login,password,shipaddress,billaddress) VALUES (1,"Rhiannon B. Reid","lacinia.orci@aodiosemper.co.uk","XIO26CSM0LK","Ap #536-9422 Dolor Ave","Ap #507-6853 Libero. Avenue"),(2,"Carissa R. Salas","Quisque.fringilla@MaurisnullaInteger.net","IQH43TVL5SQ","Ap #361-2569 Penatibus Street","Ap #917-2602 Enim. Avenue"),(3,"Kelly I. White","nulla.Donec.non@luctusfelispurus.ca","LOB27RFB9RK","P.O. Box 983, 6959 Sit Avenue","2528 Mattis Rd."),(4,"Ezra V. Reid","augue.ut.lacus@erategetipsum.net","DSB55UDC0HE","Ap #859-6451 Arcu. St.","6269 Ultrices, Street"),(5,"Iliana H. Charles","et.magnis.dis@Crasloremlorem.co.uk","PXY69RLS3HG","535-7675 Nulla Rd.","Ap #157-6806 Vitae, Avenue"),(6,"Troy Q. Ford","nibh@Cras.ca","ZMR25ERL7VE","P.O. Box 539, 3213 Quam Ave","P.O. Box 645, 6877 Cursus, Rd."),(7,"Lana C. Barlow","at.augue@duiCumsociis.edu","NEI25BZB3IR","P.O. Box 825, 5559 Vulputate Road","762-2044 Eget Ave"),(8,"Porter D. Patton","elementum@idmagna.net","CCR58SGN3HS","Ap #367-2691 In Rd.","P.O. Box 622, 9074 Ultrices. St."),(9,"Illiana V. Mueller","accumsan.interdum.libero@risusMorbi.edu","RFC02IFE2BQ","P.O. Box 893, 5056 Vestibulum Rd.","398-6400 Vitae Road"),(10,"Yvette P. Johnston","penatibus.et.magnis@vitaevelit.edu","YIX41MJQ8SU","941-9213 Vulputate Road","3663 Vel Road"),(11,"Priscilla B. Harmon","Quisque@nunc.com","RSW49JZI1MQ","Ap #580-8773 Augue, Rd.","P.O. Box 324, 6155 Pretium Street"),(12,"Myles Y. Woodard","felis.ullamcorper.viverra@tempus.com","IRZ46CNF7JG","Ap #704-1078 Lacus, Rd.","7461 Sed St."),(13,"Alyssa Y. Blackburn","arcu.et.pede@magnaatortor.net","WYD44PYX5NQ","662-111 Ac Road","305-1220 Augue Rd."),(14,"Regan C. Odonnell","id.nunc.interdum@dictum.edu","LOF39QUG4UU","305-8935 Ridiculus Ave","707-3022 Dui Ave"),(15,"Finn E. Coleman","ante.dictum.mi@et.net","ZZN87NTV8HM","415-6502 Imperdiet Rd.","8175 Metus. St."),(16,"Amy R. Robles","In.faucibus.Morbi@feugiatSed.co.uk","DEK39USU6EZ","6097 Vulputate Street","3187 Donec Ave"),(17,"Calvin F. Collier","aliquam@sagittis.edu","GXC72ENQ4MP","6241 Purus Av.","2043 Interdum. Ave"),(18,"Brandon R. Dejesus","mi@lobortis.com","QAB45NUV0QE","683-9753 Quam Av.","P.O. Box 921, 8484 Dis Road"),(19,"Hannah L. Ramsey","pede.nec.ante@eratnequenon.co.uk","NHN38IBR0RL","8843 Pulvinar Street","Ap #482-4628 Molestie Ave"),(20,"Julian M. Cardenas","dapibus.gravida@dignissim.co.uk","KKP41AVH6JI","229 Ipsum. Street","Ap #884-5074 Elit Rd."),(21,"Nola H. Shaw","risus.In.mi@purus.net","BHS59FMI7AM","915-9013 Augue Street","9542 Luctus Rd."),(22,"Amity W. Albert","dis@magna.ca","HWL24YXC7MU","209-4737 Sapien Rd.","Ap #535-8868 Facilisis Street"),(23,"Oprah K. Bush","sed.consequat@nuncid.net","WEB81PLR2RQ","4264 Nunc Street","Ap #397-4956 Amet Rd."),(24,"Abra B. Blanchard","eget.ipsum.Suspendisse@erat.co.uk","FFO99BBW3ZN","187-6547 Sit St.","584-8478 Purus. Ave"),(25,"Zelda Q. Peters","lorem.ipsum@Integer.org","WJK26DSA7EV","Ap #143-6675 Scelerisque Rd.","P.O. Box 217, 2863 Sem Ave"),(26,"India V. Albert","Donec.feugiat.metus@Donecluctusaliquet.edu","MUI38VNW6IV","379-5262 Orci Av.","Ap #697-5389 Quisque Avenue"),(27,"Chaney V. Fitzgerald","nisi.sem@nequetellus.com","PTU69MCH7OM","5372 Quis Avenue","185-862 Mollis Av."),(28,"Stephanie D. Serrano","eget.mollis@tincidunt.net","ALM41CYD4BT","5890 Arcu. St.","5603 Nunc, Ave"),(29,"Regina X. Alford","sem.ut@Praesenteunulla.org","DMI00CAD4RH","823-9973 Iaculis Rd.","P.O. Box 320, 7577 Est Av."),(30,"Diana F. Maynard","nibh.Aliquam.ornare@scelerisque.net","YAQ50UZU0ED","P.O. Box 999, 4376 Massa. Rd.","620-3696 Nam St."),(31,"Dante O. Wolf","Vestibulum.accumsan.neque@euplacerateget.com","FXK11YNB5OV","P.O. Box 438, 9317 Risus. Ave","9946 Porttitor Road"),(32,"Seth X. Hobbs","luctus.vulputate.nisi@euodiotristique.co.uk","SMK13IVX8GN","Ap #233-8366 Gravida Av.","P.O. Box 747, 3441 Interdum Av."),(33,"Amal D. Adkins","Aenean@venenatisamagna.net","UVD24PTQ0PJ","P.O. Box 206, 2785 Eu Rd.","Ap #680-3236 Elit Rd."),(34,"Damian O. Rose","Sed@cursusluctus.org","HHT47MPY2HJ","Ap #417-8100 Sed St.","790 Aenean Ave"),(35,"William D. Whitley","Vestibulum.ut@fermentumvel.edu","NAR32QEK3CW","968-1114 A Rd.","P.O. Box 375, 4031 Lacinia St."),(36,"Meredith L. Zimmerman","dui.augue@elitafeugiat.edu","URZ21FIA5ES","Ap #964-1123 Erat Avenue","2211 Libero. Av."),(37,"Oleg F. Gibson","facilisis.non.bibendum@facilisisfacilisis.org","LWD87HNM5HF","1565 Tortor, Rd.","234-3637 Ridiculus Road"),(38,"Harriet T. Hoover","amet.lorem.semper@quamdignissim.org","ZHN19SOC4SP","7378 Tortor, Rd.","P.O. Box 971, 5348 Euismod Road"),(39,"Hiram Z. Brady","amet.orci.Ut@posuere.com","OBY95PNX4ZS","P.O. Box 554, 5024 Porttitor Road","236-2936 Proin Av."),(40,"Karly T. Kent","risus.In@Nunc.org","HTU42VMP2ER","100-3520 Pharetra Avenue","P.O. Box 996, 8213 Non Street"),(41,"Iona N. Duran","eros@Utsemper.co.uk","QAY51WKS9SJ","P.O. Box 289, 619 Convallis Road","Ap #521-941 Non, Rd."),(42,"Chadwick T. Hahn","dictum.mi.ac@nonvestibulum.org","SLC65ZBI1WN","P.O. Box 832, 7896 Adipiscing Street","680 Nisi Avenue"),(43,"Natalie L. Wise","a.aliquet.vel@habitantmorbi.edu","LLM95BZR2CB","P.O. Box 357, 9005 Ridiculus Ave","Ap #815-4860 Egestas. Street"),(44,"April O. Nicholson","Donec.nibh@egestas.co.uk","ALG03XRR3AU","821-1191 Vel, St.","2236 Mauris Road"),(45,"Madaline D. Turner","dictum.magna.Ut@Duis.com","PZP42MEQ5FY","Ap #609-1771 Commodo St.","146-8946 Scelerisque Street"),(46,"Zia R. Rosales","In.scelerisque@pellentesque.org","TRX11DQD0TN","6638 Nulla St.","850-9757 Augue St."),(47,"Melanie O. Nolan","ornare.placerat.orci@Morbinequetellus.ca","RZM83REN5UE","167-2277 Ut, St.","Ap #438-647 Tempus Rd."),(48,"Hannah T. Nunez","lacus.Mauris.non@velmaurisInteger.org","WIJ01SEZ0GZ","2562 Molestie St.","Ap #171-9944 Nonummy. Street"),(49,"Solomon J. Kidd","leo.Vivamus@tristiqueaceleifend.org","BFN32GMZ9RN","Ap #426-4392 Malesuada Av.","703-2831 Interdum. Road"),(50,"Charde Q. Mcclure","nunc@tristique.ca","GLB03USI5BE","P.O. Box 545, 639 Imperdiet, Road","4176 Mauris Road"),(51,"Aidan S. Dominguez","sapien.Nunc.pulvinar@pede.ca","EBP87MZP6OS","Ap #571-2087 Sodales Road","P.O. Box 716, 9146 Vitae, Street"),(52,"Galvin V. Blanchard","elit@nulla.edu","VSL59YUR2NU","P.O. Box 116, 9342 Euismod Av.","457-1984 Malesuada Street"),(53,"Akeem W. Simmons","ac.urna@felisullamcorper.ca","IBU37YPW3XW","3883 Elit Ave","P.O. Box 334, 3815 Leo, Ave"),(54,"Tara B. Rogers","eget.ipsum@varius.org","XAW29GTX9RH","8710 Aliquam Ave","153-986 Nulla Avenue"),(55,"Justin Q. Hays","dui.lectus@Quisque.edu","MPH83MIN2ZE","Ap #860-6464 Donec Avenue","301-7453 Nec, Street"),(56,"Jack T. Bright","vel@mattisvelitjusto.com","FJB88QKW2IR","5920 Hymenaeos. Ave","718-9535 Elementum Street"),(57,"Pamela M. Hewitt","nascetur.ridiculus@enimEtiamgravida.com","XMM10BFL8PC","Ap #699-3788 Lobortis St.","307-567 Nunc Ave"),(58,"Jordan Y. Morrow","malesuada@Crasloremlorem.com","ALX83QUR1SB","Ap #770-2361 Non St.","685-5190 Enim. St."),(59,"Logan N. Bailey","elit@ipsum.co.uk","YIO07JDR7HY","P.O. Box 362, 5302 Ac Avenue","Ap #221-4193 Semper Street"),(60,"Uriah U. Jennings","metus.eu.erat@infaucibusorci.com","NMM63TAB1NN","4381 Sed Avenue","7256 Donec St."),(61,"Gannon B. Joseph","habitant.morbi@odioauctor.edu","SBT73RBN2CF","P.O. Box 670, 4485 Aliquet, St.","8761 Mauris Rd."),(62,"Scarlet T. Chang","magna.Ut.tincidunt@facilisis.co.uk","PPX17IGE0TL","P.O. Box 270, 1519 Tortor. Av.","P.O. Box 921, 3276 Id, Avenue"),(63,"Cassidy I. Lawson","elit@Duisat.net","VWY76AXR7KD","P.O. Box 500, 4137 Elit. Street","973-1714 Placerat. Avenue"),(64,"Talon T. Boone","ullamcorper@sagittis.org","JJQ56LRO3CM","Ap #399-2958 Augue Rd.","P.O. Box 731, 3405 Sit St."),(65,"Zahir C. Rosa","Curae.Donec@velvulputate.net","TOD75PAM2XJ","P.O. Box 504, 9227 Cum Rd.","732-594 Pellentesque Rd."),(66,"Gil U. Bartlett","consequat.nec.mollis@non.org","YOP78DVF7SO","P.O. Box 152, 9997 Tellus. Road","Ap #220-5238 Metus Rd."),(67,"Erasmus J. Larsen","Donec.sollicitudin@Nam.ca","KHS26GUN5HG","P.O. Box 635, 374 Lacus. Ave","942-2257 Taciti Rd."),(68,"Tatum M. Wooten","eros@Vestibulumut.ca","PSB23ATY9RX","573-9244 A Road","Ap #623-7276 Semper Street"),(69,"Keefe E. Dodson","neque.In@gravida.com","FGH87HJQ4ZW","460-4016 Cras Rd.","9359 Dapibus Street"),(70,"Susan A. Barker","consectetuer.cursus.et@esttemporbibendum.com","YZQ99DNI7RZ","Ap #967-2934 Urna, Ave","7037 Et Rd."),(71,"Troy O. Blankenship","scelerisque.sed.sapien@nonummy.org","XBA69KNZ0RL","8926 Gravida Road","P.O. Box 526, 3212 At Road"),(72,"Sybill T. Velazquez","Quisque.varius@luctusfelis.com","FOY42TEX2SC","Ap #957-5466 Eu, St.","Ap #672-7186 Non, Street"),(73,"Allegra P. Wiley","mauris@tinciduntcongueturpis.ca","VEG25EZT3PM","5351 Convallis Av.","107-7212 Suspendisse Road"),(74,"Brock T. Le","non.lobortis.quis@aliquetdiamSed.co.uk","EIK19HHR6XB","Ap #727-2138 Integer Rd.","P.O. Box 756, 6207 Consectetuer, Ave"),(75,"Price P. Bass","convallis@sitamet.net","YDC24VKO4XB","9119 Ullamcorper Rd.","Ap #796-636 Massa. Road"),(76,"Flynn L. Blevins","lorem.sit.amet@neque.net","TPX87VAN4KI","1144 Sed Av.","464-2704 Nulla St."),(77,"Michael Y. Horton","Donec.egestas@fermentumarcu.co.uk","HLD54HVD4LV","Ap #733-7147 Vitae St.","Ap #327-2352 Aliquet Road"),(78,"Mercedes M. Olsen","penatibus.et.magnis@nullaat.com","WEI05MBT8LZ","Ap #189-7617 Magnis Av.","2294 Amet St."),(79,"Katell P. Parks","elit.pellentesque@dapibusquamquis.org","UKG47DSR4YK","3261 Enim St.","161 Lacus. Ave"),(80,"Bruce J. Marshall","neque@egestasrhoncus.net","NEG98RJQ9AL","Ap #264-790 Est. Street","4847 Nec Rd."),(81,"Maile S. Prince","nunc.est@Maurisblandit.edu","XMR33ZOF9KJ","750-1383 Erat. Ave","Ap #487-4965 Purus, Avenue"),(82,"Graham H. Rasmussen","mollis.dui@loremvehicula.net","LDX08ETX6YK","Ap #418-3185 Eu St.","Ap #328-7810 Nunc Street"),(83,"Joshua V. Mathews","Nulla@ridiculus.edu","QZI41LGU3FJ","Ap #932-7444 Justo Rd.","Ap #488-4294 Rutrum, Avenue"),(84,"Carson B. Bowman","tincidunt.aliquam.arcu@mollis.co.uk","ENV47DKD4HV","P.O. Box 828, 8912 Et, Ave","Ap #859-7250 Suspendisse Ave"),(85,"Brynn B. Banks","ultrices@felisadipiscing.org","HHB10NCB4LN","Ap #299-4582 Cursus Road","P.O. Box 834, 9631 Nunc Rd."),(86,"Kristen I. Fischer","Mauris.magna@nulla.net","TFE56NFJ0HV","2248 Et St.","P.O. Box 348, 9944 Risus. St."),(87,"Ariana Z. Turner","ipsum.leo@dolorquam.org","LQH60JNO7KL","Ap #151-5302 Gravida Rd.","Ap #630-7391 Diam. Rd."),(88,"Elmo J. Burks","erat@ipsumSuspendisse.co.uk","JQM17LST0BY","3061 Aenean Ave","723-8693 Enim. Avenue"),(89,"Jenette F. Sampson","tempus.eu.ligula@Integermollis.edu","TNU60ZQL2FT","P.O. Box 675, 1025 Ligula. Road","7013 Sagittis. Rd."),(90,"Otto Y. Lindsey","diam.nunc@lectusCumsociis.org","HSI85RJT4QM","970-3571 Nunc Road","422-2801 Interdum. Road"),(91,"Joshua Z. Hebert","quis.arcu@mauris.edu","QST92YKJ3DD","Ap #488-3598 Sed, St.","860-1832 Sed Road"),(92,"Kay O. Black","dis.parturient.montes@purusin.net","FZT90DCN0TE","4191 Erat. Rd.","4554 Est Road"),(93,"Cathleen C. Bass","consectetuer.mauris@erosnectellus.ca","XGF20UWG5MS","326 Ut St.","216-6414 Vel Avenue"),(94,"Charde E. Trujillo","quis@consectetueripsumnunc.org","MMK48CDH4RA","549-7553 Quam, St.","Ap #783-783 Aenean Avenue"),(95,"Norman Q. Ayers","scelerisque.neque@erat.org","BAL40WTX8ZD","P.O. Box 783, 1298 Purus, Avenue","Ap #828-8268 Sollicitudin Ave"),(96,"Cassady F. Dillon","urna@eudolor.com","BHK83BQE4DG","P.O. Box 198, 3011 Ut Rd.","P.O. Box 775, 1561 Elit, Av."),(97,"Illiana G. Shepherd","a.dui.Cras@aliquetProin.com","XPH41OWT1YJ","P.O. Box 983, 1825 Fermentum Rd.","389-706 Scelerisque Rd."),(98,"Gary B. Jimenez","tempus.mauris@urnaVivamusmolestie.co.uk","YSC44LAX5SF","Ap #539-2989 Donec Rd.","401-2598 Vivamus Avenue"),(99,"Kessie D. Larson","dis.parturient.montes@Integer.net","BYW35WZA3DK","P.O. Box 680, 2576 Diam Road","Ap #611-1660 Sed St."),(100,"Simon A. Moon","semper@nisi.co.uk","NZF23FWA4BZ","Ap #161-3680 Eu Avenue","459-7073 Eu Ave");

INSERT INTO cs421g34.barowners
SELECT uid
FROM cs421g34.users ORDER BY uid ASC LIMIT 50;

INSERT INTO cs421g34.regcustomers
SELECT uid
FROM cs421g34.users ORDER BY uid DESC LIMIT 50;
