package org.grupo4.practica_integradora_g4.extras;

import org.grupo4.practica_integradora_g4.model.entidades.Cliente;
import org.grupo4.practica_integradora_g4.model.entidades.TipoCliente;
import org.grupo4.practica_integradora_g4.model.entidades.Usuario;

import java.math.BigDecimal;
import java.util.*;

public class Colecciones {
    private final static Map<String, String> GENEROS = new HashMap<String, String>();
    private final static Map<String, String> PREFIJOS = new LinkedHashMap<String, String>();
    private final static Map<String, String> NACIONALIDADES = new HashMap<String, String>();
    private final static Map<String, String> DEPARTAMENTOS = new LinkedHashMap<String,String>();
    private final static Map<String, Usuario> USUARIOSADMIN = new HashMap<String,Usuario>();
    private final static Map<String, String> TIPO_DOCUMENTO = new HashMap<String,String>();
    private final static Map<String, Cliente> CLIENTES = new HashMap<String,Cliente>();
    private final static List<TipoCliente> TIPOCLIENTES = new ArrayList<>();

    private static final List<Usuario> USUARIOS = new ArrayList<>();




    static {

        GENEROS.put("F","Femenino");
        GENEROS.put("M","Masculino");
        GENEROS.put("O","Otro");

        PREFIJOS.put("Sr", "Señor");
        PREFIJOS.put("Sra", "Señora");
        PREFIJOS.put("Ca", "Caballero");
        PREFIJOS.put("Dñ", "Doña");

        NACIONALIDADES.put("ES","España");
        NACIONALIDADES.put("IT","Italia");
        NACIONALIDADES.put("PT","Portugal");
        NACIONALIDADES.put("FR","Francia");
        NACIONALIDADES.put("GR","Grecia");

        TIPOCLIENTES.add(new TipoCliente("Bronce", "BR", new BigDecimal("12000.00"), new BigDecimal("5.00")));
        TIPOCLIENTES.add(new TipoCliente("Plata", "PL", new BigDecimal("20000.00"), new BigDecimal("20.00")));
        TIPOCLIENTES.add( new TipoCliente("Oro", "OR", new BigDecimal("30000.00"), new BigDecimal("40.00")));

        DEPARTAMENTOS.put("0", "Selecciona tu departamento");
        DEPARTAMENTOS.put("10", "reseach");
        DEPARTAMENTOS.put("20", "Consulting");
        DEPARTAMENTOS.put("30", "managing");
        DEPARTAMENTOS.put("40", "tecnichian");

        TIPO_DOCUMENTO.put("dni","DNI");
        TIPO_DOCUMENTO.put("pass","Pasaporte");


        USUARIOSADMIN.put("root",new Usuario("root","admin","admin","","",null,0,null,false,null,0));
        USUARIOSADMIN.put("pablo",new Usuario("pablo","huerta","huerta","","",null,0,null,false,null,0));

    }

    public static Map<String, String> getGeneros(){ return GENEROS; }
    public static Map<String, String> getGPrefijos(){ return PREFIJOS; }
    public static Map<String, String> getNacionalidades(){ return NACIONALIDADES; }
    public static Map<String, String> getDepartamentos(){ return DEPARTAMENTOS; }
    public static Map<String, Usuario> getUsuariosAdmin(){ return USUARIOSADMIN; }
    public static Map<String, String> getTipoDocumento(){ return TIPO_DOCUMENTO; }

    public static List<TipoCliente> getTIPOCLIENTES() {
        return TIPOCLIENTES;
    }

    public static List<Usuario> devuelveUsu() {
        return USUARIOS;
    }

    public static void agregarUsuario(Usuario usuario) {
        USUARIOS.add(usuario);
    }
    public static List<String> obtenerEmailUsuarios() {
        List<String> emailUsuarios = new ArrayList<>();
        for (Usuario usuario : USUARIOS) {
            emailUsuarios.add(usuario.getEmail());
        }
        return emailUsuarios;
    }

//    public static void addUsuario(Usuario u){
//        USUARIOS.put(u.getNombre(),u);
//    }

    public static void addCliente(Cliente cliente){
        CLIENTES.put(cliente.getNombre(),cliente);
    }
}