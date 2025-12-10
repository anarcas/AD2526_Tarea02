-- ------------------------------ --
-- --FP a distancia DAM---------- --
-- --Curso 25/26 ---------------- --
-- --Antonio Naranjo Castillo---- --
-- --AD Tarea 02 ---------------- --
-- --Apartado D). Procedimiento-- --
-- ------------------------------ --


DELIMITER $$
	
	-- Se verifica si el procedimiento existe antes de crearlo y se borra en tal caso
	DROP PROCEDURE IF EXISTS atletas_posicion $$
	
	-- Se crea un procedimiento para determinar el número de atletas que han terminado una carrera en una determinada posición que se pasa como argumento.
	CREATE PROCEDURE atletas_posicion (IN posicionAtleta INTEGER, OUT v_numAtletas INTEGER)
		
	BEGIN

		-- Se define la consulta y el cursor implícito
		SELECT COUNT(DISTINCT dorsal_atl) INTO v_numAtletas
		FROM competir 
		WHERE posicion = posicionAtleta;

	END $$

-- Se reestablece el delimitador
DELIMITER ;