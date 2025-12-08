/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package antonio_naranjo_ad2_e1;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.CallableStatement;
import java.util.ArrayList;
import javax.swing.JLabel;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;

/**
 * Clase principal de la interfaz gráfica de usuario (GUI) que actúa como el
 * contenedor principal (JFrame) de la aplicación.
 *
 * @author Antonio Naranjo Castillo
 */
public class ConectorJFrame extends javax.swing.JFrame {

    // Declaración de variables
    private static Connection con = null;

    private String driver;
    private String user;
    private String pass;
    private String url;
    private String connectionUrl;

    /**
     * Establece la conexión con la base de datos del campeonato de atletismo.
     * Si la conexión es exitosa, se almacena en el atributo 'conexion'. Muestra
     * un mensaje de error si la conexión falla.
     */
    public void conector() {

        // Se inicializan las variables
        driver = "com.mysql.cj.jdbc.Driver";
        user = jtfUser.getText();
        pass = jtfPassword.getText();
        url = "jdbc:mysql://localhost/campeonato_atletismo";
        connectionUrl = url + "?user=" + user + "&password=" + pass;

        try {

            // Se carga el driver JDBC para MySQL según la URL dada
            Class.forName(driver);
            // Se establece la conexión con la BD MySQL
            con = (Connection) DriverManager.getConnection(connectionUrl);
            // Otra manera de realizar la conexión
            // con = (Connection) DriverManager.getConnection(url, user, pass);

            // Si la conexión no es nula se establece la conexión.
            if (con != null) {
                jlEstado.setText("Conexión establecida");
                jlEstado.setForeground(Color.blue);
            }

            // Limpieza a fondo de los items de todos los ComboBox
            limpiarComboBox(this.getContentPane());

            // Alimentar cada combobox por sus valores únicos
            for (String comunidad : comunidadesValoresUnicos(con)) {
                jcbComunidad.addItem(comunidad);
            }

            for (String dorsal : dorsalesValoresUnicos(con)) {
                jcbDorsales.addItem(dorsal);
            }

            for (String codigo : codigoPruebaValoresUnicos(con)) {
                jcbCodigoPrueba.addItem(codigo);
            }

            for (Integer posicion : posicionValoresUnicos(con)) {
                jcbPosicion.addItem(String.valueOf(posicion));
            }

        } catch (ClassNotFoundException | SQLException e) {
            jlEstado.setText("<html>" + "Error de conexión " + e + "</html>");
            jlEstado.setForeground(Color.red);
        }
    }

    /**
     * Recupera una lista de valores únicos de la columna 'comunidad' de la
     * tabla 'universidad' de la base de datos.
     *
     * * @param con La conexión activa a la base de datos MySQL.
     * @return Una lista (ArrayList) de tipo String que contiene todos los
     * nombres de las comunidades autónomas sin duplicados.
     */
    public static ArrayList<String> comunidadesValoresUnicos(Connection con) {
        // Se define la sentencia
        String sentenciaSQL = "SELECT DISTINCT comunidad "
                            + "FROM universidad "
                            + "ORDER BY comunidad ASC";
        // Se declara e instancia una lista que recoge los valores únicos de las comunidades según la consulta anterior
        ArrayList<String> comunidades = new ArrayList<>();

        // Se realiza la consulta y se almacenan los resulstados
        try (Statement consulta = con.createStatement(); 
             ResultSet resultados = consulta.executeQuery(sentenciaSQL)) {

            while (resultados.next()) {
                String comunidad = resultados.getString("comunidad");
                comunidades.add(comunidad);
            }

            // Se muestran los resultados por consola
            System.out.println("Lista de Comunidades cargada: " + comunidades.toString());

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            System.err.printf("Se ha producido un error al ejecutar la consulta SQL.");
        }

        return comunidades;

    }

    /**
     * Ejecuta una consulta para obtener el nombre y apellidos de los atletas,
     * junto al nombre de la universidad a la que pertenecen, filtrando
     * únicamente por una comunidad autónoma específica.
     *
     * @param con La conexión activa a la base de datos MySQL.
     * @param comunidad El nombre de la comunidad autónoma utilizada como
     * criterio de filtro.
     */
    public void consultaB1(Connection con, String comunidad) {
        // Se define la sentencia
        String sentenciaSQL = "SELECT a.nombre, a.apellidos, u.nombre as nomUniv "
                            + "FROM atleta a, universidad u "
                            + "WHERE a.universidad = u.codigo "
                            + "AND u.comunidad = ?";
        // Se declara e instancia una lista que recoge una lista de resultados
        ArrayList<String> listaResultados = new ArrayList<>();

        // Se realiza la consulta y se almacenan los resulstados
        try (PreparedStatement consulta = con.prepareStatement(sentenciaSQL);) {

            consulta.setString(1, comunidad);
            ResultSet resultados = consulta.executeQuery();

            while (resultados.next()) {
                String nombreA = resultados.getString("nombre");
                String apellidosA = resultados.getString("apellidos");
                String nombreU = resultados.getString("nomUniv");
                String resultado = String.format("El atleta %s %s pertenece a la %s.", nombreA, apellidosA, nombreU);
                System.out.println(resultado);
                listaResultados.add(resultado);
            }

            // Se muestran los resultados en la aplicación swing
            mostrarlistaJLabel(listaResultados, jlConsultaB1);
            jlConsultaB1.setForeground(Color.blue);
            // Se cierra el objeto ResultSet
            resultados.close();
        
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            System.err.printf("Se ha producido un error al ejecutar la consulta SQL.");
        }

    }

    /**
     * Muestra el contenido de una lista de cadenas ArrayList dentro de un
     * componente JLabel.
     *
     * La implementación utiliza etiquetas HTML internas para asegurar que cada
     * elemento de la lista se presente en una línea separada dentro de la
     * etiqueta.
     *
     * @param lista La lista de cadenas que se desea mostrar.
     * @param jLabel El componente JLabel de destino donde se mostrará la lista.
     */
    public static void mostrarlistaJLabel(ArrayList<String> lista, JLabel jLabel) {
        // Se instancia un constructor de String para formar un texto formateado empleando HTML
        StringBuilder sb = new StringBuilder();
        
        // Abrir etiqueta html
        sb.append("<html>");
        // Contenido de la lista más salto de línea
        for (String str : lista) {
            sb.append(str).append("<br>");
        }
        // Cerrar etiqueta html
        sb.append("</html>");
        // Alimentar la etiqueta JLabel
        jLabel.setText(sb.toString());
    }

    /**
     * Recupera una lista de valores únicos de los dorsales de los atletas a
     * partir de los resultados registrados en la tabla 'competir'.
     *
     * @param con La conexión activa a la base de datos MySQL.
     * @return Una lista (ArrayList) de tipo String que contiene todos los
     * dorsales de los atletas que han competido, sin duplicados.
     */
    public static ArrayList<String> dorsalesValoresUnicos(Connection con) {
        // Se define la consulta
        String sentenciaSQL = "SELECT DISTINCT dorsal_atl "
                            + "FROM competir "
                            + "ORDER BY dorsal_atl ASC";
        // Se declara/instancia la lista que almacenará los valores únicos de los dorsales
        ArrayList<String> dorsales = new ArrayList<>();

        // Se realiza la consulta y se almacenan los resultados
        try (Statement consulta = con.createStatement(); 
             ResultSet resultados = consulta.executeQuery(sentenciaSQL)) {

            while (resultados.next()) {
                String dorsal = resultados.getString("dorsal_atl");
                dorsales.add(dorsal);
            }

            // Se muestran los resultados por consola
            System.out.println("Lista de dorsales existentes con puntos: " + dorsales.toString());

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            System.err.printf("Se ha producido un error al ejecutar la consulta SQL.");
        }

        return dorsales;

    }

    /**
     * Ejecuta una consulta para calcular y obtener la cantidad total de puntos
     * que ha obtenido un atleta específico en todas las pruebas en las que ha
     * competido.
     *
     * @param con La conexión activa a la base de datos MySQL.
     * @param dorsal El dorsal (identificador) del atleta cuyo total de puntos
     * se desea calcular.
     */
    public void consultaB2(Connection con, String dorsal) {

        // Se declaran e inician variables auxiliares
        Integer puntos = 0;
        String mensajeSalida = "";

        // Se define la sentencia de la consulta
        String sentenciaSQL = "SELECT sum(puntos) as puntosTotales "
                            + "FROM competir "
                            + "WHERE dorsal_atl = ?";

        // Se realiza la consulta y se obtiene el resultado
        try (PreparedStatement consulta = con.prepareStatement(sentenciaSQL)) {

            consulta.setString(1, dorsal);
            ResultSet resultados = consulta.executeQuery();

            while (resultados.next()) {
                puntos = resultados.getInt("puntosTotales");
                mensajeSalida = String.format("El atleta con dorsal nº %s ha conseguido %d puntos.", dorsal, puntos);
                System.out.println(mensajeSalida);
            }

            // Se muestra el resultado en la aplicación swing
            jlConsultaB2.setText(mensajeSalida);
            jlConsultaB2.setForeground(Color.blue);
            // Se cierra el objeto ResultSet
            resultados.close();
            
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            System.err.printf("Se ha producido un error al ejecutar la consulta SQL.");
        }

    }

    /**
     * Recupera una lista de valores únicos de los códigos de las pruebas
     * registradas en la tabla 'PRUEBAS'.
     *
     * @param con La conexión activa a la base de datos MySQL.
     * @return Una lista (ArrayList) de tipo String que contiene todos los
     * códigos de las pruebas sin duplicados.
     */
    public static ArrayList<String> codigoPruebaValoresUnicos(Connection con) {
        // Se define la sentencia
        String sentenciaSQL = "SELECT DISTINCT codigo "
                            + "FROM prueba "
                            + "ORDER BY codigo ASC";
        // Se instancia una lista que almacenará los valores únicos de los códigos
        ArrayList<String> codigos = new ArrayList<>();

        // Se realiza la consulta y se almacenan los resultados obtenidos
        try (Statement consulta = con.createStatement(); 
             ResultSet resultados = consulta.executeQuery(sentenciaSQL)) {

            while (resultados.next()) {
                String codigo = resultados.getString("codigo");
                codigos.add(codigo);
            }

            // Se imprimen por consola los resultados
            System.out.println("Lista de códigos de pruebas existentes: " + codigos.toString());

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            System.err.printf("Se ha producido un error al ejecutar la consulta SQL.");
        }

        return codigos;

    }

    /**
     * Modifica la distancia de una prueba determinada en la base de datos
     * mediante una sentencia UPDATE parametrizada.
     *
     * @param con La conexión activa a la base de datos MySQL.
     * @param codigo El código (identificador) de la prueba cuya distancia se
     * desea actualizar.
     * @param distancia El nuevo valor de la distancia que se asignará a la
     * prueba.
     */
    public void actualizacionC(Connection con, String codigo, Integer distancia) {
        // Se define la consulta
        String sentenciaSQL = "UPDATE prueba SET distancia = ? "
                            + "WHERE codigo = ?";

        // Se realiza la consulta y se obtiene el resultado
        try (PreparedStatement consulta = con.prepareStatement(sentenciaSQL)) {

            consulta.setInt(1, distancia);
            consulta.setString(2, codigo);
            int regActualizados = consulta.executeUpdate();

            // Se muestra el resultado por consola
            System.out.println(String.format("Se %s actualizado %d %s.", (regActualizados <= 1) ? "ha" : "han",regActualizados, (regActualizados <= 1) ? "registro" : "registros"));

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            System.err.printf("Se ha producido un error al ejecutar la consulta SQL.");
        }

    }

    /**
     * Recupera y muestra la distancia de una prueba determinada identificada
     * por su código.
     *
     * @param con La conexión activa a la base de datos MySQL.
     * @param codigo El código (identificador) de la prueba que se desea
     * consultar.
     */
    public void consultaC(Connection con, String codigo) {
        // Se inicializa una variable auxiliar
        String mensajeSalida = null;
        // Se define la sentencia de la consulta
        String sentenciaSQL = "SELECT distancia "
                            + "FROM prueba "
                            + "WHERE codigo = ?";

        // Se realiza la consulta y se almacen
        try (PreparedStatement consulta = con.prepareStatement(sentenciaSQL)) {

            consulta.setString(1, codigo);
            ResultSet resultados = consulta.executeQuery();

            while (resultados.next()) {
                Integer distancia = resultados.getInt("distancia");
                mensajeSalida = String.format("La distancia de la prueba nº %s ha sido actualizada a %d m.", codigo, distancia);
                // Se muestran los resultados por consola
                System.out.println(mensajeSalida);
            }

            // Se muestran los resultados en la aplicación swing
            jlEjercicioC.setText(mensajeSalida);
            jlEjercicioC.setForeground(Color.blue);
            // Se cierra el objeto ResultSet
            resultados.close();
            
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            System.err.printf("Se ha producido un error al ejecutar la consulta SQL.");
        }

    }

    /**
     * Llama al procedimiento almacenado 'atletas_posicion' en la base de datos
     * para calcular la cantidad de atletas que obtuvieron una posición
     * específica.
     *
     * Este método invoca el procedimiento, pasa el valor de la posición como
     * parámetro de entrada (IN) y recupera el conteo de atletas a través de un
     * parámetro de salida (OUT).
     *
     * @param con La conexión activa a la base de datos MySQL.
     * @param posicion El valor de la posición utilizada como criterio de
     * búsqueda (parámetro IN).
     */
    public void procedimientoD(Connection con, String posicion) {

        // Se declaran variables auxiliares
        Integer numAtletas;
        String mensajeSalida;
        Integer posicionAtleta=Integer.parseInt(posicion);
        // Se define la llamada al procedimiento
        String llamadaProcedimiento = "{ call atletas_posicion(?, ?) }";

        // Se realiza la llamada al procedimiento y se recogen los resultados
        try (CallableStatement prcProcedimientoNumAtletas = con.prepareCall(llamadaProcedimiento)) {
            // Se ejecuta el procedimiento
            prcProcedimientoNumAtletas.setInt(1, posicionAtleta);
            prcProcedimientoNumAtletas.registerOutParameter(2, java.sql.Types.INTEGER);
            prcProcedimientoNumAtletas.execute();
            // Se obtiene el resultado
            numAtletas=prcProcedimientoNumAtletas.getInt(2);
            // Se muestra el resultado por pantalla
            mensajeSalida=String.format("El número de atletas que han acabado en la posición nº %d = %d.", posicionAtleta,numAtletas);
            System.out.println(mensajeSalida);
            // Se muestra el resultado en la aplicación swing
            jlProcedimientoD.setText(mensajeSalida);
            jlProcedimientoD.setForeground(Color.blue);
            
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            System.err.printf("Se ha producido un error al ejecutar la consulta SQL.");
        }

    }
 
    
    /**
     * Recupera una lista de valores únicos de las posiciones obtenidos por los
     * atletas, registrados en la tabla 'competir'.
     *
     * @param con La conexión activa a la base de datos MySQL.
     * @return Una lista (ArrayList) de tipo Integer que contiene todas las
     * posiciones únicas de las pruebas, ordenadas de forma ascendente.
     */
    public static ArrayList<Integer> posicionValoresUnicos(Connection con) {
        // Se define la consulta
        String sentenciaSQL = "SELECT DISTINCT posicion "
                            + "FROM competir "
                            + "WHERE posicion > 0 "
                            + "ORDER BY posicion ASC";
        // Se instancia una lista para almacenar los valores únicos de las posiciones
        ArrayList<Integer> posiciones = new ArrayList<>();

        // Se realiza la consulta y se almacenan los resultados
        try (Statement consulta = con.createStatement(); 
             ResultSet resultados = consulta.executeQuery(sentenciaSQL)) {

            while (resultados.next()) {
                Integer posicion = resultados.getInt("posicion");
                posiciones.add(posicion);
            }

            // Se muestran los resultados por consola
            System.out.println("Lista de posiciones de pruebas existentes: " + posiciones.toString());

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            System.err.printf("Se ha producido un error al ejecutar la consulta SQL.");
        }

        return posiciones;

    }
    
    /**
     * Cierra la conexión activa con la base de datos.Asegura que el objeto
     * Connection} se cierre correctamente, liberando los recursos del sistema y
     * de la base de datos.
     *
     * @param con La conexión activa a la base de datos MySQL que se desea
     * cerrar.
     */
    public void desconectar(Connection con) {
        // Si existe conexión o no es nula cierra la conexión
        if (con != null) {
            try {
                con.close();
                // Se muestra el resultado en la aplicación swing
                jlEstado.setText("Conexión cerrada");
                jlEstado.setForeground(Color.red);
            } catch (SQLException ex) {
                Logger.getLogger(ConectorJFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    /**
     * Limpia (elimina todos los items) de todos los componentes JComboBox
     * anidados dentro del contenedor especificado y sus subcontenedores.
     *
     * @param contenedor El componente contenedor desde donde se inicia la
     * limpieza.
     */
    public static void limpiarComboBox(Container contenedor) {

        // Obtener todos los componentes del contenedor
        Component[] componentes = contenedor.getComponents();
        JComboBox<?> comboBox;

        // Por cada componente del contenedor si es un ComboBox limpia sus items
        for (Component componente : componentes) {

            if (componente instanceof JComboBox) {
                comboBox = (JComboBox<?>) componente;
                comboBox.removeAllItems();
                // Se reestablece su primer item
                comboBox.getItemAt(0);
            }

            // Limpieza recursiva en caso que un componente sea otro contenedor
            if (componente instanceof Container) {
                limpiarComboBox((Container) componente);
            }
        }
    }
    
    /**
     * Creates new form ConectorJFrame
     */
    public ConectorJFrame() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jbSalir = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jlUser = new javax.swing.JLabel();
        jtfUser = new javax.swing.JTextField();
        jlPassword = new javax.swing.JLabel();
        jtfPassword = new javax.swing.JTextField();
        jbConectar = new javax.swing.JButton();
        jlEstado = new javax.swing.JLabel();
        jbDesconectar = new javax.swing.JButton();
        jlEjercicio01 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jlEjercicio2 = new javax.swing.JLabel();
        jlComunidad = new javax.swing.JLabel();
        jcbComunidad = new javax.swing.JComboBox<>();
        jbConsultaB1 = new javax.swing.JButton();
        jlConsultaB1 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jlEjercicio3 = new javax.swing.JLabel();
        jlDorsales = new javax.swing.JLabel();
        jcbDorsales = new javax.swing.JComboBox<>();
        jbConsultaB2 = new javax.swing.JButton();
        jlConsultaB2 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jlCodigoPrueba = new javax.swing.JLabel();
        jlEjercicio4 = new javax.swing.JLabel();
        jcbCodigoPrueba = new javax.swing.JComboBox<>();
        jlDistancia = new javax.swing.JLabel();
        jtfDistancia = new javax.swing.JTextField();
        jbEjercicioC = new javax.swing.JButton();
        jlEjercicioC = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jlEjercicio5 = new javax.swing.JLabel();
        jlEjercicio6 = new javax.swing.JLabel();
        jlPosicion = new javax.swing.JLabel();
        jcbPosicion = new javax.swing.JComboBox<>();
        jbProcedimientoD = new javax.swing.JButton();
        jlProcedimientoD = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jbSalir.setText("Salir y desconectar");
        jbSalir.setToolTipText("Pulsa para salir del programa y desconectar de la base de datos");
        jbSalir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbSalirActionPerformed(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jlUser.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jlUser.setText("Usuario:");

        jtfUser.setText("root");
        jtfUser.setToolTipText("Nombre de usuario");

        jlPassword.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jlPassword.setText("Contraseña:");
        jlPassword.setToolTipText("");

        jtfPassword.setText("root");
        jtfPassword.setToolTipText("Introduzca la contraseña");

        jbConectar.setText("Conectar");
        jbConectar.setToolTipText("Pulsa para establecer la conexión.");
        jbConectar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbConectarActionPerformed(evt);
            }
        });

        jlEstado.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jlEstado.setText("Estado de la conexión");
        jlEstado.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jlEstado.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jlEstado.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jlEstado.setRequestFocusEnabled(false);
        jlEstado.setVerticalTextPosition(javax.swing.SwingConstants.TOP);

        jbDesconectar.setText("Desconectar");
        jbDesconectar.setToolTipText("Pulsa para cerrar la conexión con la base de datos.");
        jbDesconectar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbDesconectarActionPerformed(evt);
            }
        });

        jlEjercicio01.setFont(new java.awt.Font("Roboto Mono", 1, 12)); // NOI18N
        jlEjercicio01.setText("A.- Establecer conexión con la base de datos");
        jlEjercicio01.setToolTipText("");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jlUser, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jlPassword, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(26, 26, 26)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jlEstado, javax.swing.GroupLayout.PREFERRED_SIZE, 253, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addComponent(jbConectar, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jbDesconectar))
                                .addComponent(jtfUser, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jtfPassword, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jlEjercicio01, javax.swing.GroupLayout.PREFERRED_SIZE, 345, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(16, Short.MAX_VALUE))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(jlEjercicio01)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jlUser, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jtfUser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jlPassword, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtfPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jbConectar)
                    .addComponent(jbDesconectar))
                .addGap(18, 18, 18)
                .addComponent(jlEstado, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jlEjercicio2.setFont(new java.awt.Font("Roboto Mono", 1, 12)); // NOI18N
        jlEjercicio2.setText("B1.- Nombre y apellidos de los atletas junto al nombre de la universidad a la que pertenecen");
        jlEjercicio2.setToolTipText("");

        jlComunidad.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jlComunidad.setText("Comunidad:");

        jcbComunidad.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jcbComunidad.setToolTipText("Selecciona la comunidad a la que pertenece la universidad");

        jbConsultaB1.setText("Consultar atletas");
        jbConsultaB1.setToolTipText("Pulsar para consultar los atletas que pertenecen a la comunidad anterior");
        jbConsultaB1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbConsultaB1ActionPerformed(evt);
            }
        });

        jlConsultaB1.setText("Datos personales de los atletas pertenecientes a la comunidad a consultar.");
        jlConsultaB1.setToolTipText("Resultado de la consulta");
        jlConsultaB1.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jlConsultaB1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jlEjercicio2, javax.swing.GroupLayout.PREFERRED_SIZE, 658, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jlComunidad, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jbConsultaB1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jcbComunidad, javax.swing.GroupLayout.PREFERRED_SIZE, 334, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jlConsultaB1, javax.swing.GroupLayout.PREFERRED_SIZE, 509, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(15, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(jlEjercicio2)
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jlComunidad)
                    .addComponent(jcbComunidad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jbConsultaB1)
                    .addComponent(jlConsultaB1, javax.swing.GroupLayout.PREFERRED_SIZE, 212, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jlEjercicio3.setFont(new java.awt.Font("Roboto Mono", 1, 12)); // NOI18N
        jlEjercicio3.setText("B2.- Puntos totales que ha obtenido un determinado atleta");
        jlEjercicio3.setToolTipText("");

        jlDorsales.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jlDorsales.setText("Dorsal:");

        jcbDorsales.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jcbDorsales.setToolTipText("Selecciona el dorsal del atleta a consultar");

        jbConsultaB2.setText("Consultar puntos");
        jbConsultaB2.setToolTipText("Pulsar para consultar los puntos del dorsal del atleta anterior");
        jbConsultaB2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbConsultaB2ActionPerformed(evt);
            }
        });

        jlConsultaB2.setText("Puntos obtenidos del dorsal anterior");
        jlConsultaB2.setToolTipText("Resultado de la consulta");
        jlConsultaB2.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jlConsultaB2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jlEjercicio3, javax.swing.GroupLayout.PREFERRED_SIZE, 414, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jlDorsales, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jbConsultaB2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                                .addComponent(jlConsultaB2, javax.swing.GroupLayout.PREFERRED_SIZE, 346, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(19, 19, 19))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                                .addComponent(jcbDorsales, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(215, 215, 215))))))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(jlEjercicio3)
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jlDorsales)
                    .addComponent(jcbDorsales, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jbConsultaB2)
                    .addComponent(jlConsultaB2, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(24, Short.MAX_VALUE))
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jlCodigoPrueba.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jlCodigoPrueba.setText("Código de la prueba:");

        jlEjercicio4.setFont(new java.awt.Font("Roboto Mono", 1, 12)); // NOI18N
        jlEjercicio4.setText("C.- Modificar la distancia de una prueba determinada indicada por su código");
        jlEjercicio4.setToolTipText("");

        jcbCodigoPrueba.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jcbCodigoPrueba.setToolTipText("Selecciona el código de la prueba a consultar");

        jlDistancia.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jlDistancia.setText("Distancia a actualizar:");

        jtfDistancia.setToolTipText("Introduzca la nueva distancia de la prueba");

        jbEjercicioC.setText("Actualizar distancia");
        jbEjercicioC.setToolTipText("Pulsar para actualizar la distancia en la prueba con el código definido anteriormente");
        jbEjercicioC.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbEjercicioCActionPerformed(evt);
            }
        });

        jlEjercicioC.setText("Distancia actualizada");
        jlEjercicioC.setToolTipText("Resultado de la actualización");
        jlEjercicioC.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jlEjercicioC.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jlEjercicio4, javax.swing.GroupLayout.PREFERRED_SIZE, 548, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jlCodigoPrueba, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jbEjercicioC, javax.swing.GroupLayout.DEFAULT_SIZE, 144, Short.MAX_VALUE)
                            .addComponent(jlDistancia, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(35, 35, 35)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jtfDistancia, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jcbCodigoPrueba, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jlEjercicioC, javax.swing.GroupLayout.PREFERRED_SIZE, 660, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(43, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(jlEjercicio4)
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jlCodigoPrueba)
                    .addComponent(jcbCodigoPrueba, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jlDistancia)
                    .addComponent(jtfDistancia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jbEjercicioC)
                    .addComponent(jlEjercicioC, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel5.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jlEjercicio5.setFont(new java.awt.Font("Roboto Mono", 1, 12)); // NOI18N
        jlEjercicio5.setText("D.- Procedimiento atletas_posicion");
        jlEjercicio5.setToolTipText("");

        jlEjercicio6.setFont(new java.awt.Font("Roboto Mono", 0, 12)); // NOI18N
        jlEjercicio6.setText("Número total de atletas que han acabado en una determinada posición");
        jlEjercicio6.setToolTipText("");

        jlPosicion.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jlPosicion.setText("Posición:");

        jcbPosicion.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jcbPosicion.setToolTipText("Selecciona la posición a consultar");

        jbProcedimientoD.setText("Contar atletas");
        jbProcedimientoD.setToolTipText("Pulsar para contar el número de atletas que han acabado en la posición seleccionada.");
        jbProcedimientoD.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbProcedimientoDActionPerformed(evt);
            }
        });

        jlProcedimientoD.setText("Número de atletas que han terminado en la posición indicada.");
        jlProcedimientoD.setToolTipText("Resultado del recuento de atletas");
        jlProcedimientoD.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jlProcedimientoD.setBorder(javax.swing.BorderFactory.createCompoundBorder());

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap(22, Short.MAX_VALUE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(jlEjercicio5, javax.swing.GroupLayout.PREFERRED_SIZE, 499, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jlEjercicio6))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jlPosicion, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jbProcedimientoD, javax.swing.GroupLayout.DEFAULT_SIZE, 144, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 40, Short.MAX_VALUE)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jlProcedimientoD, javax.swing.GroupLayout.PREFERRED_SIZE, 348, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jcbPosicion, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(25, 25, 25))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addComponent(jlEjercicio5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jlEjercicio6)
                .addGap(18, 18, 18)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jlPosicion)
                    .addComponent(jcbPosicion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jbProcedimientoD)
                    .addComponent(jlProcedimientoD, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(16, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(jbSalir)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(18, 18, 18)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))))
                .addContainerGap(20, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addComponent(jbSalir)
                .addContainerGap(27, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jbConectarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbConectarActionPerformed
        // TODO add your handling code here:
        // Se llama al método conector
        conector();
    }//GEN-LAST:event_jbConectarActionPerformed

    private void jbConsultaB1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbConsultaB1ActionPerformed
        // TODO add your handling code here:
        // Se declaran variables auxiliares
        int item;
        String comunidadSeleccionada;
        item = jcbComunidad.getSelectedIndex();
        comunidadSeleccionada = jcbComunidad.getItemAt(item);
        // Se ejecuta la consulta B1
        consultaB1(con, comunidadSeleccionada);
    }//GEN-LAST:event_jbConsultaB1ActionPerformed

    private void jbConsultaB2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbConsultaB2ActionPerformed
        // TODO add your handling code here:
        // Se declaran variables auxiliares
        int item;
        String dorsalSeleccionado;
        item = jcbDorsales.getSelectedIndex();
        dorsalSeleccionado = jcbDorsales.getItemAt(item);
        // Se ejecuta la consulta B2
        consultaB2(con, dorsalSeleccionado);
    }//GEN-LAST:event_jbConsultaB2ActionPerformed

    private void jbEjercicioCActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbEjercicioCActionPerformed
        // TODO add your handling code here:
        // Se declaran variables auxiliares
        int item;
        String codigoSeleccionado;
        item = jcbCodigoPrueba.getSelectedIndex();
        codigoSeleccionado = jcbCodigoPrueba.getItemAt(item);
        String mensajeEx;
        // Se ejecuta la actualización C2 y la posterior consulta C2
        try {
            Integer distancia = Integer.parseInt(jtfDistancia.getText());
            actualizacionC(con, codigoSeleccionado, distancia);
            consultaC(con, codigoSeleccionado);
        } catch (NumberFormatException ex) {
            // Se capta la excepción y se avisa al usario
            mensajeEx=String.format("El dato ingresado por el usuario no se ha podido convertir a número entero. Error: %s",ex.getMessage());
            System.err.println(mensajeEx);
            // Se emplea un objeto JOptionPane para avisar al usuario
            JOptionPane.showMessageDialog(this, mensajeEx);
            // Se emplea una etiqueta en la aplicación swing para avisar al usuario
            jlEjercicioC.setText(mensajeEx);
            jlEjercicioC.setForeground(Color.red);
        }

    }//GEN-LAST:event_jbEjercicioCActionPerformed

    private void jbDesconectarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbDesconectarActionPerformed
        // TODO add your handling code here:
        // Se llama al método desconectar
        desconectar(con);
    }//GEN-LAST:event_jbDesconectarActionPerformed

    private void jbSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbSalirActionPerformed
        // TODO add your handling code here:
        // Primero se desconecta y luego se sale del programa
        desconectar(con);
        System.exit(0);
    }//GEN-LAST:event_jbSalirActionPerformed

    private void jbProcedimientoDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbProcedimientoDActionPerformed
        // TODO add your handling code here:
        // Se declaran variables auxiliares
        int item;
        String posicionSeleccionada;
        item = jcbPosicion.getSelectedIndex();
        posicionSeleccionada = jcbPosicion.getItemAt(item);
        // Se ejecuta el procedimiento D
        procedimientoD(con,posicionSeleccionada);
    }//GEN-LAST:event_jbProcedimientoDActionPerformed

    /**
     * Método principal de la aplicación. Punto de entrada para la ejecución de
     * la aplicación de escritorio.
     *
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ConectorJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ConectorJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ConectorJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ConectorJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ConectorJFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JButton jbConectar;
    private javax.swing.JButton jbConsultaB1;
    private javax.swing.JButton jbConsultaB2;
    private javax.swing.JButton jbDesconectar;
    private javax.swing.JButton jbEjercicioC;
    private javax.swing.JButton jbProcedimientoD;
    private javax.swing.JButton jbSalir;
    private javax.swing.JComboBox<String> jcbCodigoPrueba;
    private javax.swing.JComboBox<String> jcbComunidad;
    private javax.swing.JComboBox<String> jcbDorsales;
    private javax.swing.JComboBox<String> jcbPosicion;
    private javax.swing.JLabel jlCodigoPrueba;
    private javax.swing.JLabel jlComunidad;
    private javax.swing.JLabel jlConsultaB1;
    private javax.swing.JLabel jlConsultaB2;
    private javax.swing.JLabel jlDistancia;
    private javax.swing.JLabel jlDorsales;
    private javax.swing.JLabel jlEjercicio01;
    private javax.swing.JLabel jlEjercicio2;
    private javax.swing.JLabel jlEjercicio3;
    private javax.swing.JLabel jlEjercicio4;
    private javax.swing.JLabel jlEjercicio5;
    private javax.swing.JLabel jlEjercicio6;
    private javax.swing.JLabel jlEjercicioC;
    private javax.swing.JLabel jlEstado;
    private javax.swing.JLabel jlPassword;
    private javax.swing.JLabel jlPosicion;
    private javax.swing.JLabel jlProcedimientoD;
    private javax.swing.JLabel jlUser;
    private javax.swing.JTextField jtfDistancia;
    private javax.swing.JTextField jtfPassword;
    private javax.swing.JTextField jtfUser;
    // End of variables declaration//GEN-END:variables
}
