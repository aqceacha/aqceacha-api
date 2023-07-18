Create Table vendedor(
    idven bigint not null auto_increment primary key,
    nomeven varchar(60) not null,
    imgven varchar(255) null,
    publiimg varchar(255) null,
    emailven varchar(60) not null,
    senhaven varchar(20) not null,
    apelidoven varchar(20) not null,
    telefoneven varchar(15) not null,
    enderecoven varchar(40) not null,
    complementoven varchar(6) null,
    documentoven varchar(60) not null,
    cnpj varchar(60) null,
    idcidade bigint not null,
    idramo bigint not null,
    precovenda decimal(8,2)
);