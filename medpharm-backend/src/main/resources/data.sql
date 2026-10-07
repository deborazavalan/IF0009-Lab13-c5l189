INSERT INTO usuarios (username, password, nombre_completo, rol) VALUES
('dra.salas',  '$2a$10$jCwrUIGDBgGCUvJYB/IsxuwI9I12fu/.cEep0hDrIDli.IpgGx4TO', 'Dra. Laura Salas Mora',     'MEDICO'),
('dr.vargas',  '$2a$10$jCwrUIGDBgGCUvJYB/IsxuwI9I12fu/.cEep0hDrIDli.IpgGx4TO', 'Dr. Andrés Vargas Quesada', 'MEDICO'),
('farma.rojas','$2a$10$9bxlMs3c/Lj7fuo8pm0cmuMVuT6uUsh71bq3cr/y0LaJ5xQAGiWem', 'Marcela Rojas Jiménez',     'FARMACEUTICO');

INSERT INTO medicamentos (codigo, nombre, stock, precio_unitario) VALUES
('MED-001', 'Acetaminofén 500 mg',      200, 0.15),
('MED-002', 'Ibuprofeno 400 mg',        150, 0.25),
('MED-003', 'Amoxicilina 500 mg',        80, 0.60),
('MED-004', 'Loratadina 10 mg',         120, 0.20),
('MED-005', 'Omeprazol 20 mg',           90, 0.35),
('MED-006', 'Metformina 850 mg',         60, 0.30),
('MED-007', 'Losartán 50 mg',            70, 0.40),
('MED-008', 'Salbutamol inhalador',      25, 6.50);

INSERT INTO recetas (codigo_receta, paciente_nombre, medico_id, estado, fecha_emision) VALUES
('RX-2026-0001', 'Carlos Mora Jiménez',  (SELECT id FROM usuarios WHERE username = 'dra.salas'), 'PENDIENTE',  TIMESTAMP '2026-10-01 08:30:00'),
('RX-2026-0002', 'Sofía Araya Campos',   (SELECT id FROM usuarios WHERE username = 'dr.vargas'), 'DESPACHADA', TIMESTAMP '2026-10-02 10:15:00'),
('RX-2026-0003', 'Luis Fernández Solano',(SELECT id FROM usuarios WHERE username = 'dra.salas'), 'PENDIENTE',  TIMESTAMP '2026-10-03 14:45:00');

INSERT INTO detalles_receta (receta_id, medicamento_id, cantidad, dosis_indicada) VALUES
((SELECT id FROM recetas WHERE codigo_receta = 'RX-2026-0001'), (SELECT id FROM medicamentos WHERE codigo = 'MED-001'), 20, '1 tableta cada 8 horas'),
((SELECT id FROM recetas WHERE codigo_receta = 'RX-2026-0001'), (SELECT id FROM medicamentos WHERE codigo = 'MED-005'), 14, '1 cápsula en ayunas'),
((SELECT id FROM recetas WHERE codigo_receta = 'RX-2026-0002'), (SELECT id FROM medicamentos WHERE codigo = 'MED-003'), 21, '1 cápsula cada 8 horas por 7 días'),
((SELECT id FROM recetas WHERE codigo_receta = 'RX-2026-0003'), (SELECT id FROM medicamentos WHERE codigo = 'MED-006'), 30, '1 tableta con el almuerzo'),
((SELECT id FROM recetas WHERE codigo_receta = 'RX-2026-0003'), (SELECT id FROM medicamentos WHERE codigo = 'MED-007'), 30, '1 tableta cada mañana');
