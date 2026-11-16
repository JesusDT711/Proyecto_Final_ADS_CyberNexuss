-- =====================================================================
-- DATOS DE PRUEBA · Sistema de ventas Sumac Yanahuara
-- Ejecutar UNA sola vez, con el backend ya arrancado (tablas creadas).
-- phpMyAdmin: base sistema_ventas > pestaña SQL > pegar > Continuar.
-- Para empezar de cero, usa el bloque de LIMPIEZA al final del archivo.
-- =====================================================================
USE sistema_ventas;

-- ---------- PRODUCTOS (30; 1 inactivo; 6 con stock <= mínimo) ----------
INSERT IGNORE INTO productos (codigo, nombre, descripcion, precio, stock, stock_minimo, categoria, activo, fecha_creacion) VALUES
('CEM-001','Cemento Sol 42.5 kg','Bolsa de cemento Portland tipo I',28.50,180,40,'Construcción',TRUE,NOW()),
('CEM-002','Cemento Andino tipo V 42.5 kg','Cemento resistente a sulfatos',29.90,95,30,'Construcción',TRUE,NOW()),
('ARE-001','Arena gruesa saco 40 kg','Arena para mezcla y asentado',6.50,300,80,'Construcción',TRUE,NOW()),
('LAD-001','Ladrillo King Kong 18 huecos','Ladrillo de arcilla',1.35,2500,500,'Construcción',TRUE,NOW()),
('FIE-012','Fierro corrugado 1/2" x 9 m','Acero corrugado grado 60',38.00,140,50,'Construcción',TRUE,NOW()),
('FIE-038','Fierro corrugado 3/8" x 9 m','Acero corrugado grado 60',21.50,18,40,'Construcción',TRUE,NOW()),
('ALA-016','Alambre negro N°16 (kg)','Alambre recocido para amarre',5.80,90,25,'Construcción',TRUE,NOW()),
('CLA-003','Clavos 3" (kg)','Clavos para madera',6.20,60,20,'Ferretería',TRUE,NOW()),
('YES-001','Yeso bolsa 25 kg','Yeso para acabados',14.00,22,25,'Construcción',TRUE,NOW()),
('PVC-004','Tubo PVC desagüe 4" x 3 m','Tubería de desagüe',35.00,55,15,'Fontanería',TRUE,NOW()),
('PVC-002','Tubo PVC agua 1/2" x 5 m','Tubería de agua fría',9.80,120,30,'Fontanería',TRUE,NOW()),
('COD-004','Codo PVC 4" x 90°','Accesorio de desagüe',4.50,200,50,'Fontanería',TRUE,NOW()),
('LLA-012','Llave de paso 1/2" bronce','Llave de paso',18.50,12,12,'Fontanería',TRUE,NOW()),
('GRI-001','Grifo lavatorio cromado','Grifería para lavatorio',42.00,25,8,'Fontanería',TRUE,NOW()),
('TAN-600','Tanque de agua 600 L','Tanque de polietileno',389.00,6,3,'Fontanería',TRUE,NOW()),
('TER-050','Terma eléctrica 50 L','Terma de acumulación',459.00,9,4,'Calefacción',TRUE,NOW()),
('CAB-214','Cable THW 14 AWG rollo 100 m','Conductor eléctrico',129.00,30,10,'Eléctrico',TRUE,NOW()),
('INT-001','Interruptor simple','Interruptor empotrable',8.90,150,40,'Eléctrico',TRUE,NOW()),
('TOM-001','Tomacorriente doble','Tomacorriente con tierra',12.50,130,40,'Eléctrico',TRUE,NOW()),
('FOC-LED','Foco LED 9 W','Luz cálida',7.50,4,30,'Eléctrico',TRUE,NOW()),
('PIN-101','Pintura látex blanca 1 gal','Pintura interior',62.90,8,10,'Pinturas',TRUE,NOW()),
('PIN-102','Pintura esmalte sintético 1 gal','Esmalte para metal y madera',78.00,24,10,'Pinturas',TRUE,NOW()),
('THI-001','Thinner acrílico 1 gal','Diluyente',29.00,30,10,'Pinturas',TRUE,NOW()),
('BRO-004','Brocha 4"','Brocha de cerda',11.00,45,15,'Pinturas',TRUE,NOW()),
('MAR-016','Martillo carpintero 16 oz','Mango de fibra',32.00,20,6,'Herramientas',TRUE,NOW()),
('ALI-008','Alicate universal 8"','Alicate aislado',27.50,14,5,'Herramientas',TRUE,NOW()),
('TAL-710','Taladro percutor 710 W','Taladro con maletín',215.00,5,3,'Herramientas',TRUE,NOW()),
('DIS-007','Disco de corte 7"','Disco para amoladora',9.50,0,20,'Herramientas',TRUE,NOW()),
('CAN-050','Candado 50 mm','Candado de seguridad',19.00,35,10,'Ferretería',TRUE,NOW()),
('CIN-001','Cinta teflón','Cinta para roscas',1.50,300,100,'Fontanería',TRUE,NOW()),
('TEC-OLD','Tecle manual (descontinuado)','Producto inactivo de prueba',99.00,3,2,'Herramientas',FALSE,NOW());

-- ---------- CLIENTES (12; uno sin teléfono ni correo) ----------
INSERT INTO clientes (nombre, documento, telefono, correo, direccion) VALUES
('Constructora Andina SAC','20512345678','987654321','compras@andina.pe','Av. Ejército 700, Yanahuara, Arequipa'),
('Inmobiliaria Los Cedros SAC','20601234567','954123456','logistica@loscedros.pe','Calle Los Cedros 210, Cayma, Arequipa'),
('Ferretería El Maestro EIRL','20456789012','958741236','elmaestro@mail.com','Jr. Palacio Viejo 345, Arequipa'),
('Consorcio Vial Rímac','20387654321','945678123','tesoreria@consorciorimac.pe','Av. Alfonso Ugarte 890, Breña, Lima'),
('Juan Pérez Quispe','45871236','912345678','juan.perez@mail.com','Jr. Los Pinos 456, Yanahuara, Arequipa'),
('María Torres Salas','41236587','965478123','maria.torres@mail.com','Calle Mercaderes 120, Arequipa'),
('Carlos Mamani Condori','70123456','974185236','cmamani@mail.com','Urb. Cayma Mz B Lt 5, Arequipa'),
('Ana Gutiérrez Rojas','43219876','936985214','ana.gutierrez@mail.com','Av. Dolores 300, Arequipa'),
('Corporación Sur Perú SAC','20598761234','952369874','ventas@surperu.pe','Parque Industrial Río Seco, Arequipa'),
('Edificaciones Misti SRL','20534567890','987412365','contacto@edmisti.pe','Av. Goyeneche 455, Arequipa'),
('Rosa Huamán Paredes','29876543',NULL,NULL,'Calle Bolívar 87, Cayma, Arequipa'),
('Servicios Generales Volcán SAC','20578901234','921456387','admin@sgvolcan.pe','Av. Jesús 1020, Arequipa');

-- ---------- PROVEEDORES (6; uno inactivo) ----------
INSERT INTO proveedores (nombre, ruc, telefono, correo, direccion, activo) VALUES
('Distribuidora Cementera del Sur SAC','20511111111','054-251001','ventas@cementerasur.pe','Av. Industrial 100, Arequipa',TRUE),
('Aceros del Pacífico SAC','20522222222','01-4561234','pedidos@acerospacifico.pe','Av. Argentina 2500, Callao',TRUE),
('Tuberías y Accesorios Peruanos SA','20533333333','01-3345566','ventas@tapsa.pe','Av. Colonial 1800, Lima',TRUE),
('Eléctricos Andinos SRL','20544444444','054-263322','comercial@electricosandinos.pe','Calle Jerusalén 410, Arequipa',TRUE),
('Pinturas y Acabados Arequipa SAC','20555555555','054-271144','ventas@pinturasaqp.pe','Av. Venezuela 900, Arequipa',TRUE),
('Herramientas Pro Import EIRL','20566666666','01-2255889','info@herrapro.pe','Av. Industrial 750, Lima',FALSE);

-- ---------- PEDIDOS (15): 8 confirmados, 7 pendientes ----------
INSERT INTO pedidos (cliente_id, fecha_pedido, estado, total, observaciones)
SELECT c.id, DATE_SUB(NOW(), INTERVAL x.d DAY), x.est, 0, x.tag FROM (
 SELECT 'SEED-P01' tag,'20512345678' doc,35 d,'CONFIRMADO' est UNION ALL
 SELECT 'SEED-P02','20601234567',30,'CONFIRMADO' UNION ALL
 SELECT 'SEED-P03','20456789012',26,'CONFIRMADO' UNION ALL
 SELECT 'SEED-P04','20387654321',21,'CONFIRMADO' UNION ALL
 SELECT 'SEED-P05','45871236',18,'CONFIRMADO' UNION ALL
 SELECT 'SEED-P06','41236587',14,'CONFIRMADO' UNION ALL
 SELECT 'SEED-P07','20598761234',10,'CONFIRMADO' UNION ALL
 SELECT 'SEED-P08','20534567890',7,'CONFIRMADO' UNION ALL
 SELECT 'SEED-P09','70123456',5,'PENDIENTE' UNION ALL
 SELECT 'SEED-P10','43219876',3,'PENDIENTE' UNION ALL
 SELECT 'SEED-P11','20578901234',2,'PENDIENTE' UNION ALL
 SELECT 'SEED-P12','29876543',1,'PENDIENTE' UNION ALL
 SELECT 'SEED-P13','20512345678',0,'PENDIENTE' UNION ALL
 SELECT 'SEED-P14','20601234567',0,'PENDIENTE' UNION ALL
 SELECT 'SEED-P15','70123456',0,'PENDIENTE'
) x JOIN clientes c ON c.documento = x.doc;

CREATE TEMPORARY TABLE tmp_det (tag VARCHAR(20), codigo VARCHAR(50), cant INT);
INSERT INTO tmp_det VALUES
('SEED-P01','CEM-001',60),('SEED-P01','FIE-012',40),('SEED-P01','ALA-016',10),
('SEED-P02','LAD-001',1000),('SEED-P02','CEM-002',30),('SEED-P02','ARE-001',50),
('SEED-P03','CIN-001',50),('SEED-P03','CAN-050',10),('SEED-P03','MAR-016',5),
('SEED-P04','FIE-012',60),('SEED-P04','FIE-038',40),('SEED-P04','CLA-003',15),
('SEED-P05','PIN-101',4),('SEED-P05','BRO-004',6),('SEED-P05','THI-001',2),
('SEED-P06','GRI-001',2),('SEED-P06','LLA-012',4),('SEED-P06','PVC-002',6),
('SEED-P07','CAB-214',6),('SEED-P07','INT-001',30),('SEED-P07','TOM-001',30),('SEED-P07','FOC-LED',20),
('SEED-P08','TAN-600',2),('SEED-P08','PVC-004',10),('SEED-P08','COD-004',20),
('SEED-P09','CEM-001',10),('SEED-P09','ARE-001',20),
('SEED-P10','TER-050',6),('SEED-P10','GRI-001',1),
('SEED-P11','PIN-102',6),('SEED-P11','BRO-004',6),('SEED-P11','THI-001',4),
('SEED-P12','MAR-016',1),('SEED-P12','ALI-008',1),('SEED-P12','CAN-050',2),
('SEED-P13','CEM-001',500),
('SEED-P14','TAL-710',1),('SEED-P14','DIS-007',5);
-- SEED-P15 no tiene detalles a propósito.

INSERT INTO detalles_pedido (pedido_id, producto_id, cantidad, precio_unitario, subtotal)
SELECT p.id, pr.id, t.cant, pr.precio, pr.precio * t.cant
FROM tmp_det t JOIN pedidos p ON p.observaciones = t.tag JOIN productos pr ON pr.codigo = t.codigo;
DROP TEMPORARY TABLE tmp_det;

UPDATE pedidos p SET total = (SELECT COALESCE(SUM(d.subtotal),0) FROM detalles_pedido d WHERE d.pedido_id = p.id)
WHERE p.observaciones LIKE 'SEED-P%';

-- ---------- ENTREGAS (6; P07 y P08 quedan SIN entrega para programarlas) ----------
INSERT INTO entregas (pedido_id, direccion_entrega, fecha_programada, fecha_entrega, estado, responsable, observaciones)
SELECT p.id, c.direccion, DATE_ADD(NOW(), INTERVAL x.prog DAY), DATE_ADD(NOW(), INTERVAL x.ent DAY), x.est, x.resp, x.obs FROM (
 SELECT 'SEED-P01' tag,'ENTREGADA' est,-33 prog,-32 ent,'Luis Ramos' resp,'Entregado en obra, firmado por el residente' obs UNION ALL
 SELECT 'SEED-P02','ENTREGADA',-28,-28,'Luis Ramos','Entrega completa' UNION ALL
 SELECT 'SEED-P03','ENTREGADA',-24,-23,'Pedro Salas','Recogido en tienda' UNION ALL
 SELECT 'SEED-P04','EN_CAMINO',-1,NULL,'Pedro Salas','Salió a las 8:00 a. m.' UNION ALL
 SELECT 'SEED-P05','PENDIENTE',2,NULL,'Luis Ramos','Coordinar horario con el cliente' UNION ALL
 SELECT 'SEED-P06','CANCELADA',-10,NULL,'Pedro Salas','Cliente reprogramó la compra'
) x JOIN pedidos p ON p.observaciones = x.tag JOIN clientes c ON c.id = p.cliente_id;

-- ---------- MOVIMIENTOS DE INVENTARIO (16) ----------
INSERT INTO movimientos_inventario (producto_id, tipo_movimiento, cantidad, fecha_movimiento, observacion)
SELECT pr.id, x.tipo, x.cant, DATE_SUB(NOW(), INTERVAL x.d DAY), x.obs FROM (
 SELECT 'CEM-001' cod,'ENTRADA' tipo,200 cant,40 d,'Compra inicial de cemento' obs UNION ALL
 SELECT 'FIE-012','ENTRADA',150,40,'Compra inicial de fierro' UNION ALL
 SELECT 'LAD-001','ENTRADA',3000,38,'Compra de ladrillos' UNION ALL
 SELECT 'PVC-004','ENTRADA',60,36,'Reposición de tubería' UNION ALL
 SELECT 'CAB-214','ENTRADA',40,34,'Reposición de cable' UNION ALL
 SELECT 'CEM-001','SALIDA',20,30,'Bolsas dañadas en almacén' UNION ALL
 SELECT 'PIN-101','SALIDA',2,25,'Envases abiertos (merma)' UNION ALL
 SELECT 'PIN-102','ENTRADA',30,20,'Reposición de pintura' UNION ALL
 SELECT 'FOC-LED','SALIDA',6,15,'Focos rotos en almacén' UNION ALL
 SELECT 'ARE-001','ENTRADA',300,12,'Ingreso de arena' UNION ALL
 SELECT 'DIS-007','SALIDA',10,9,'Uso interno en taller' UNION ALL
 SELECT 'TER-050','ENTRADA',10,8,'Ingreso de termas' UNION ALL
 SELECT 'ARE-001','SALIDA',15,6,'Material para obra propia' UNION ALL
 SELECT 'GRI-001','ENTRADA',30,4,'Reposición de grifería' UNION ALL
 SELECT 'LLA-012','SALIDA',3,2,'Muestras para cliente' UNION ALL
 SELECT 'THI-001','ENTRADA',30,1,'Reposición de thinner'
) x JOIN productos pr ON pr.codigo = x.cod;

-- ---------- SOLICITUDES DE ABASTECIMIENTO (5: 2 recibidas, 3 pendientes) ----------
INSERT INTO solicitudes_abastecimiento (proveedor_id, fecha_solicitud, estado, observaciones)
SELECT pv.id, DATE_SUB(NOW(), INTERVAL x.d DAY), x.est, x.tag FROM (
 SELECT 'SEED-S01' tag,'20522222222' ruc,30 d,'RECIBIDA' est UNION ALL
 SELECT 'SEED-S02','20511111111',25,'RECIBIDA' UNION ALL
 SELECT 'SEED-S03','20555555555',4,'PENDIENTE' UNION ALL
 SELECT 'SEED-S04','20544444444',2,'PENDIENTE' UNION ALL
 SELECT 'SEED-S05','20533333333',1,'PENDIENTE'
) x JOIN proveedores pv ON pv.ruc = x.ruc;

INSERT INTO detalles_abastecimiento (solicitud_id, producto_id, cantidad, costo_unitario)
SELECT s.id, pr.id, x.cant, x.costo FROM (
 SELECT 'SEED-S01' tag,'FIE-012' cod,150 cant,31.00 costo UNION ALL
 SELECT 'SEED-S01','FIE-038',80,17.50 UNION ALL
 SELECT 'SEED-S01','ALA-016',40,4.60 UNION ALL
 SELECT 'SEED-S02','CEM-001',200,24.80 UNION ALL
 SELECT 'SEED-S02','CEM-002',100,25.90 UNION ALL
 SELECT 'SEED-S03','PIN-101',30,51.00 UNION ALL
 SELECT 'SEED-S03','PIN-102',20,64.00 UNION ALL
 SELECT 'SEED-S03','THI-001',20,23.50 UNION ALL
 SELECT 'SEED-S04','FOC-LED',100,5.20 UNION ALL
 SELECT 'SEED-S04','INT-001',60,6.80 UNION ALL
 SELECT 'SEED-S05','LLA-012',24,14.00 UNION ALL
 SELECT 'SEED-S05','PVC-004',30,28.50
) x JOIN solicitudes_abastecimiento s ON s.observaciones = x.tag JOIN productos pr ON pr.codigo = x.cod;

-- ---------- ALERTAS DE STOCK ----------
INSERT INTO alertas_stock (producto_id, mensaje, atendida, fecha_creacion)
SELECT id, CONCAT('Stock bajo para el producto ', nombre, '. Disponible: ', stock, ' unidades. Stock mínimo: ', stock_minimo, ' unidades.'), FALSE, NOW()
FROM productos WHERE stock <= stock_minimo AND activo = TRUE;
INSERT INTO alertas_stock (producto_id, mensaje, atendida, fecha_creacion)
SELECT id, CONCAT('Stock bajo para el producto ', nombre, '. Disponible: 70 unidades. Stock mínimo: ', stock_minimo, ' unidades.'), TRUE, DATE_SUB(NOW(), INTERVAL 20 DAY)
FROM productos WHERE codigo = 'ARE-001';

-- =====================================================================
-- LIMPIEZA (opcional): borra TODOS los datos de negocio y deja usuarios y roles.
-- Quita los "-- " del inicio de las líneas para ejecutarla.
-- =====================================================================
-- SET FOREIGN_KEY_CHECKS = 0;
-- TRUNCATE alertas_stock; TRUNCATE entregas; TRUNCATE detalles_pedido; TRUNCATE pedidos;
-- TRUNCATE detalles_abastecimiento; TRUNCATE solicitudes_abastecimiento; TRUNCATE movimientos_inventario;
-- TRUNCATE productos; TRUNCATE clientes; TRUNCATE proveedores;
-- SET FOREIGN_KEY_CHECKS = 1;
