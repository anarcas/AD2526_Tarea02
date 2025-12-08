/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package antonio_naranjo_ad2_e2;

import java.awt.Color;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import javax.swing.JLabel;

/**
 *
 * @author anaranjo
 */
public class ConectorJFrame extends javax.swing.JFrame {

    private static Connection con = null;

    private String driver;
    private String user;
    private String pass;
    private String url;
    

    public void conector() {

        driver = "com.mysql.cj.jdbc.Driver";
        user = jtfUser.getText();
        pass = jtfPassword.getText();
        url = "jdbc:mysql://localhost/campeonato_atletismo";

        try {

            Class.forName(driver);
            con = (Connection) DriverManager.getConnection(url, user, pass);

            if (con != null) {
                jlEstado.setText("Conexión establecida");
                jlEstado.setForeground(Color.blue);
            }
            
            metodoA(con);

            // Alimentar ComboBoxes 
            
            // Limpiar ComboBox de comunidades únicas
            jcbComunidad.removeAllItems();
            
            for (String comunidad : comunidadesValoresUnicos(con)) {
                jcbComunidad.addItem(comunidad);
            }
            
            // Limpiar ComboBox de dorsales únicos
            jcbDorsales.removeAllItems();
            
            for (String dorsal : dorsalesValoresUnicos(con)) {
                jcbDorsales.addItem(dorsal);
            }
            
            // Limpiar ComboBox de dorsales únicos
            jcbCodigoPrueba.removeAllItems();
            
            for (String codigo : codigoPruebaValoresUnicos(con)) {
                jcbCodigoPrueba.addItem(codigo);
            }

        } catch (ClassNotFoundException | SQLException e) {
            jlEstado.setText("<html>" + "Error de conexión " + e + "</html>");
            jlEstado.setForeground(Color.red);
        }
    }

    public static ArrayList<String> comunidadesValoresUnicos(Connection con) {
        String sentenciaSQL = "SELECT DISTINCT comunidad FROM universidad";
        ArrayList<String> comunidades = new ArrayList<>();
        
        try (Statement consulta = con.createStatement(); 
             ResultSet resultados = consulta.executeQuery(sentenciaSQL)) {

            while (resultados.next()) {
                String comunidad = resultados.getString("comunidad");
                comunidades.add(comunidad);
            }

            System.out.println("Lista de Comunidades cargada: " + comunidades.toString());

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            System.err.printf("Se ha producido un error al ejecutar la consulta SQL.");
        }
        
        return comunidades;
        
    }
    
     public static void metodoA(Connection con) {
        String sentenciaSQL = "SELECT * FROM atleta";
        try (Statement consulta = con.createStatement(); 
             ResultSet resultados = consulta.executeQuery(sentenciaSQL)) {

            while (resultados.next()) {
                String nombreA = resultados.getString("nombre");
                String apellidosA = resultados.getString("apellidos");
                String dorsalA = resultados.getString("dorsal");
                System.out.println(String.format("El atleta %s %s tiene el dorsal nº %s.", nombreA, apellidosA, dorsalA));
            }

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            System.err.printf("Se ha producido un error al ejecutar la consulta SQL.");
        }

    }
     
    /**
     *
     * @param con
     */
    public void consultaB1(Connection con) {

        int item;
        String comunidadSeleccionada;
        item = jcbComunidad.getSelectedIndex();
        comunidadSeleccionada = jcbComunidad.getItemAt(item);

        ArrayList<String> listaResultados = new ArrayList<>();
        
        String sentenciaSQL = String.format("SELECT a.nombre, a.apellidos, u.nombre as nomUniv FROM atleta a, universidad u WHERE a.universidad=u.codigo AND u.comunidad = '%s';", comunidadSeleccionada);

        try (Statement consulta = con.createStatement(); 
             ResultSet resultados = consulta.executeQuery(sentenciaSQL)) {

            while (resultados.next()) {
                String nombreA = resultados.getString("nombre");
                String apellidosA = resultados.getString("apellidos");
                String nombreU = resultados.getString("nomUniv");
                String resultado = String.format("El atleta %s %s pertenece a la %s.", nombreA, apellidosA, nombreU);
                System.out.println(resultado);
                listaResultados.add(resultado);
            }

            mostrarlistaJLabel(listaResultados, jlConsultaB1);

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            System.err.printf("Se ha producido un error al ejecutar la consulta SQL.");
        }

    }
     
    public static void mostrarlistaJLabel(ArrayList<String> lista, JLabel jLabel) {
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
    
    
     public static ArrayList<String> dorsalesValoresUnicos(Connection con) {
        String sentenciaSQL = "SELECT DISTINCT dorsal_atl FROM competir";
        ArrayList<String> dorsales = new ArrayList<>();
        
        try (Statement consulta = con.createStatement(); 
             ResultSet resultados = consulta.executeQuery(sentenciaSQL)) {

            while (resultados.next()) {
                String dorsal = resultados.getString("dorsal_atl");
                dorsales.add(dorsal);
            }

            System.out.println("Lista de dorsales existentes con puntos: " + dorsales.toString());

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            System.err.printf("Se ha producido un error al ejecutar la consulta SQL.");
        }
        
        return dorsales;
        
    }
    
      /**
     *
     * @param con
     */
    public void consultaB2(Connection con) {

        int item;
        String dorsalSeleccionado;
        item = jcbDorsales.getSelectedIndex();
        dorsalSeleccionado = jcbDorsales.getItemAt(item);
        Integer puntos=0;
        String mensajeSalida="";
        
        String sentenciaSQL = String.format("SELECT sum(puntos) as puntosTotales FROM competir WHERE dorsal_atl='%s';", dorsalSeleccionado);
        
        try (Statement consulta = con.createStatement(); 
             ResultSet resultados = consulta.executeQuery(sentenciaSQL)) {

            while (resultados.next()) {
                puntos = resultados.getInt("puntosTotales");
                mensajeSalida=String.format("El atleta con dorsal nº %s ha conseguido %d puntos.",dorsalSeleccionado, puntos);
                System.out.println(mensajeSalida);
            }

            jlConsultaB2.setText(mensajeSalida);

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            System.err.printf("Se ha producido un error al ejecutar la consulta SQL.");
        }

    }
     
    
    public static ArrayList<String> codigoPruebaValoresUnicos(Connection con) {
        String sentenciaSQL = "SELECT DISTINCT codigo FROM prueba";
        ArrayList<String> codigos = new ArrayList<>();
        
        try (Statement consulta = con.createStatement(); 
             ResultSet resultados = consulta.executeQuery(sentenciaSQL)) {

            while (resultados.next()) {
                String codigo = resultados.getString("codigo");
                codigos.add(codigo);
            }

            System.out.println("Lista de códigos de pruebas existentes: " + codigos.toString());

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            System.err.printf("Se ha producido un error al ejecutar la consulta SQL.");
        }
        
        return codigos;
        
    }
     
    public void actualizacionC(Connection con) {

        int item;
        String codigoSeleccionado;
        item = jcbCodigoPrueba.getSelectedIndex();
        codigoSeleccionado = jcbCodigoPrueba.getItemAt(item);
        Integer distancia=Integer.parseInt(jtfDistancia.getText());

        
        String sentenciaSQL = String.format("UPDATE prueba SET distancia=%d WHERE codigo='%s';", distancia,codigoSeleccionado);
        
        try (Statement consulta = con.createStatement(); 
             ResultSet resultados = consulta.executeQuery(sentenciaSQL)) {

            while (resultados.next()) {
                puntos = resultados.getInt("puntosTotales");
                mensajeSalida=String.format("El atleta con dorsal nº %s ha conseguido %d puntos.",dorsalSeleccionado, puntos);
                System.out.println(mensajeSalida);
            }

            jlConsultaB2.setText(mensajeSalida);

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            System.err.printf("Se ha producido un error al ejecutar la consulta SQL.");
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

        jlUser = new javax.swing.JLabel();
        jtfUser = new javax.swing.JTextField();
        jlPassword = new javax.swing.JLabel();
        jtfPassword = new javax.swing.JTextField();
        jbConectar = new javax.swing.JButton();
        jlEstado = new javax.swing.JLabel();
        jlComunidad = new javax.swing.JLabel();
        jcbComunidad = new javax.swing.JComboBox<>();
        jbConsultaB1 = new javax.swing.JButton();
        jlConsultaB2 = new javax.swing.JLabel();
        jlDorsales = new javax.swing.JLabel();
        jcbCodigoPrueba = new javax.swing.JComboBox<>();
        jbConsultaB2 = new javax.swing.JButton();
        jlConsultaB1 = new javax.swing.JLabel();
        jlCodigoPrueba = new javax.swing.JLabel();
        jcbDorsales = new javax.swing.JComboBox<>();
        jbEjercicioC = new javax.swing.JButton();
        jlEjercicioC = new javax.swing.JLabel();
        jlDistancia = new javax.swing.JLabel();
        jtfDistancia = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

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

        jlEstado.setText("Estado de la conexión");
        jlEstado.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jlEstado.setRequestFocusEnabled(false);

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

        jlConsultaB2.setText("Puntos obtenidos del dorsal anterior");
        jlConsultaB2.setToolTipText("Resultado de la consulta");
        jlConsultaB2.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        jlDorsales.setText("Dorsal:");

        jcbCodigoPrueba.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jcbCodigoPrueba.setToolTipText("Selecciona el código de la prueba a consultar");

        jbConsultaB2.setText("Consultar puntos");
        jbConsultaB2.setToolTipText("Pulsar para consultar los puntos del dorsal del atleta anterior");
        jbConsultaB2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbConsultaB2ActionPerformed(evt);
            }
        });

        jlConsultaB1.setText("Datos personales de los atletas pertenecientes a la comunidad a consultar.");
        jlConsultaB1.setToolTipText("Resultado de la consulta");
        jlConsultaB1.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        jlCodigoPrueba.setText("Código de la prueba:");

        jcbDorsales.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jcbDorsales.setToolTipText("Selecciona el dorsal del atleta a consultar");

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

        jlDistancia.setText("Distancia a actualizar:");

        jtfDistancia.setToolTipText("Introduzca la nueva distancia de la prueba");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(50, 50, 50)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jlPassword, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jbConectar)
                                .addComponent(jlUser, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(jlComunidad)
                            .addComponent(jbConsultaB1)
                            .addComponent(jlDorsales)
                            .addComponent(jbConsultaB2))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(33, 33, 33)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                        .addComponent(jtfPassword, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE)
                                        .addComponent(jtfUser, javax.swing.GroupLayout.Alignment.LEADING))
                                    .addComponent(jlEstado, javax.swing.GroupLayout.PREFERRED_SIZE, 660, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jcbComunidad, javax.swing.GroupLayout.PREFERRED_SIZE, 426, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jlConsultaB2, javax.swing.GroupLayout.PREFERRED_SIZE, 380, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(41, 41, 41)
                                .addComponent(jcbDorsales, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jlCodigoPrueba)
                            .addComponent(jbEjercicioC)
                            .addComponent(jlDistancia))
                        .addGap(30, 30, 30)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jlEjercicioC, javax.swing.GroupLayout.PREFERRED_SIZE, 352, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(jtfDistancia, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jcbCodigoPrueba, javax.swing.GroupLayout.Alignment.LEADING, 0, 150, Short.MAX_VALUE)))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                    .addContainerGap(214, Short.MAX_VALUE)
                    .addComponent(jlConsultaB1, javax.swing.GroupLayout.PREFERRED_SIZE, 590, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(66, 66, 66)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(49, 49, 49)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jtfUser)
                    .addComponent(jlUser, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(22, 22, 22)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jlPassword, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtfPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(38, 38, 38)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jbConectar)
                    .addComponent(jlEstado, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jlComunidad)
                    .addComponent(jcbComunidad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jbConsultaB1)
                .addGap(85, 85, 85)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jlDorsales)
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jlConsultaB2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jbConsultaB2))
                        .addGap(30, 30, 30)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jlCodigoPrueba)
                            .addComponent(jcbCodigoPrueba, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jlDistancia)
                            .addComponent(jtfDistancia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jlEjercicioC, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jbEjercicioC))
                        .addGap(142, 142, 142))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jcbDorsales, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(265, 265, 265)
                    .addComponent(jlConsultaB1, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(347, Short.MAX_VALUE)))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jbConectarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbConectarActionPerformed
        // TODO add your handling code here:
        conector();
    }//GEN-LAST:event_jbConectarActionPerformed

    private void jbConsultaB1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbConsultaB1ActionPerformed
        // TODO add your handling code here:
        consultaB1(con);
    }//GEN-LAST:event_jbConsultaB1ActionPerformed

    private void jbConsultaB2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbConsultaB2ActionPerformed
        // TODO add your handling code here:
        consultaB2(con);
    }//GEN-LAST:event_jbConsultaB2ActionPerformed

    private void jbEjercicioCActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbEjercicioCActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jbEjercicioCActionPerformed

    /**
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
    private javax.swing.JButton jbConectar;
    private javax.swing.JButton jbConsultaB1;
    private javax.swing.JButton jbConsultaB2;
    private javax.swing.JButton jbEjercicioC;
    private javax.swing.JComboBox<String> jcbCodigoPrueba;
    private javax.swing.JComboBox<String> jcbComunidad;
    private javax.swing.JComboBox<String> jcbDorsales;
    private javax.swing.JLabel jlCodigoPrueba;
    private javax.swing.JLabel jlComunidad;
    private javax.swing.JLabel jlConsultaB1;
    private javax.swing.JLabel jlConsultaB2;
    private javax.swing.JLabel jlDistancia;
    private javax.swing.JLabel jlDorsales;
    private javax.swing.JLabel jlEjercicioC;
    private javax.swing.JLabel jlEstado;
    private javax.swing.JLabel jlPassword;
    private javax.swing.JLabel jlUser;
    private javax.swing.JTextField jtfDistancia;
    private javax.swing.JTextField jtfPassword;
    private javax.swing.JTextField jtfUser;
    // End of variables declaration//GEN-END:variables
}
