package org.example.models;

import org.example.exceptions.DineroInsuicienteException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.*;
import org.junit.jupiter.api.Nested;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Properties;
import java.util.function.BiFunction;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;

class CuentaTest {
    Cuenta cuenta;

    //Metodos de ciclo de vida

    //Indicamos que se debe ejecutar antes de cada metodo, si tenemos esto no tendremos que crear la cuenta para cada test
    @BeforeEach
    void initMetodoTest(){
        System.out.println("Iniciando el metodo");

        //Con esto podriamos crear una instancia cuenta para todos los metodos
        this.cuenta = new Cuenta("Andres", new BigDecimal(1000.12345));
    }

    //Se ejecutara despues de cada test
    @AfterEach
    void tearDown() {
        System.out.println("Finalizando el metodo de prueba");
    }

    //Antes de que se cree la instancia, comun para todos los metodos y se ejecuta una sola vez
    @BeforeAll
    static void beforeAll() {
        //Podemos usarlo para iniciarlizar algun recurso necesario para el test
        System.out.println("Inicializando en test");
    }

    //Se ejecuta desoues de que se hayan finalizado todos los metodos de prueba
    @AfterAll
    static void afterAll() {
        System.out.println("Finalizando el test");
    }

    @Nested
    class CuentaOperacionesTest{
    @Test
    @Disabled //Con disable el test se saltara por si estamos testeando algo sin implementar del todo
    @DisplayName("Probando nombre de la cuenta corriente")
    void testNombreCuenta(){
        fail();
        //Damos los datos a el objeto
        Cuenta cuenta = new Cuenta("Andres", new BigDecimal(1000.12345));
//        cuenta.setPersona("Andres");
        String esperado = "Andres";
        String real = cuenta.getPersona();
        assertNotNull(real,"La cuenta no puede ser nula");
        assertEquals(esperado,real,"El nombre de la cuenta no es el que se esperaba, se esperaba: "+esperado + "sin embargo fue "+ real);
        //Comparamos y comprobamos que los datos esten bien
        Assertions.assertEquals(esperado,real);
        assertTrue(real.equals("Andres"), "Nombre de la cuenta esperada debe ser igual a la real");
    }

    @Test
    @DisplayName("Probando el saldo de la cuenta")
    void testSaldoCuenta(){
        Cuenta cuenta = new Cuenta("Andres",new BigDecimal("1000.12345"));
        assertNotNull(cuenta.getSaldo());
        assertEquals(1000.12345,cuenta.getSaldo().doubleValue());
        assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
        assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    @DisplayName("Probando la referenciad de la cuenta")
    void testReferenciaCuenta() {
        Cuenta cuenta = new Cuenta("John Doe", new BigDecimal("8900.9997"));
        Cuenta cuenta2 = new Cuenta("John Doe", new BigDecimal("8900.9997"));

//        Comparamos las cuentas teniendo los mismo valores
//        assertNotEquals(cuenta2,cuenta);

        //Si sobreescribimos el equals de la clase comparara con este
        assertEquals(cuenta2,cuenta);
    }

    @Test
    @DisplayName("Probando el dibuto de la cuenta evitando nulos y negativos")
    void testDebitoCuenta(){
        Cuenta cuenta = new Cuenta("Andres", new BigDecimal("1000.12345"));
        cuenta.debito(new BigDecimal(100));
        assertNotNull(cuenta.getSaldo());
        assertEquals(900,cuenta.getSaldo().intValue());
        assertEquals("900.12345",cuenta.getSaldo().toPlainString());
    }

    @Test
    @DisplayName("Probando el credito de la cuenta y controlando que se tiene dinero")
    void testCreditoCuenta(){
        Cuenta cuenta = new Cuenta("Andres", new BigDecimal("1000.12345"));
        cuenta.credito(new BigDecimal(100));
        assertNotNull(cuenta.getSaldo());
        assertEquals(1100,cuenta.getSaldo().intValue());
        assertEquals("1100.12345",cuenta.getSaldo().toPlainString());
    }

//    Producimos el error de forma intencional para comprobar que funciona
    @Test
    @DisplayName("Probando la excepcion de dinero insuficiente en la cuenta")
    void testDineroInsuficienteExceptionCuenta(){
        Cuenta cuenta = new Cuenta("Andres",new BigDecimal("1000.12345"));
        Exception exception = assertThrows(DineroInsuicienteException.class, () ->{
            cuenta.debito(new BigDecimal(1500));
        });
        String actual = exception.getMessage();
        String esperado = "Dinero insuficiente";

        assertEquals(esperado,actual);
    }

    @Test
    @DisplayName("Probando el tranferir dinero entre varias cuentas de un banco")
    void testTransferirDineroCuentas(){
        Cuenta cuenta = new Cuenta("Jhon Doe", new BigDecimal("2500"));
        Cuenta cuenta2 = new Cuenta("Joao", new BigDecimal("1500.1234"));

        Banco banco = new Banco("");
        banco.setNombre("Banco del Estado");
        banco.transferir(cuenta2,cuenta,new BigDecimal(500));
        assertEquals("1000.1234",cuenta2.getSaldo().toPlainString());
        assertEquals("3000",cuenta.getSaldo().toPlainString());
    }

    @Test
    @DisplayName("testeando la relacion entre cuentas de banco")
    void testRelacionBancoCuentas(){
        Cuenta cuenta = new Cuenta("Jhon Doe", new BigDecimal("2500"));
        Cuenta cuenta2 = new Cuenta("Joao", new BigDecimal("1500.1234"));

        Banco banco = new Banco("");
        banco.addCuenta(cuenta);
        banco.addCuenta(cuenta2);

        banco.setNombre("Banco del Estado");
        banco.transferir(cuenta2,cuenta,new BigDecimal(500));

//        Podemos ponerlo todo en un assert all para que se compruebe todo y no se paren los test ak fallar uno
        assertAll(() -> {assertEquals("1000.1234",cuenta2.getSaldo().toPlainString(), () -> "El valor del saldo de la cuenta 2 no es la esperada");},
                    () -> { assertEquals("3000",cuenta.getSaldo().toPlainString());},
                () -> { assertEquals(2,banco.getCuentas().size());},
                () -> {assertEquals("Banco del Estado", cuenta.getBanco().getNombre());},
                () -> {assertEquals("Joao", banco.getCuentas().stream()
                        .filter(c -> c.getPersona().equals("Joao"))
                        .findFirst()
                        .get().getPersona());},
                () -> {assertTrue(banco.getCuentas().stream().anyMatch(c -> c.getPersona().equals("Joao")));});

//        assertEquals("1000.1234",cuenta2.getSaldo().toPlainString());
//        assertEquals("3000",cuenta.getSaldo().toPlainString());

//        assertEquals(2,banco.getCuentas().size());
//        assertEquals("Banco del Estado", cuenta.getBanco().getNombre());
//        assertEquals("Joao", banco.getCuentas().stream()
//                .filter(c -> c.getPersona().equals("Joao"))
//                .findFirst()
//                .get().getPersona());

//        assertTrue(banco.getCuentas().stream().anyMatch(c -> c.getPersona().equals("Joao")));
    }
    }

    @Nested
    class OperacionesDelOS {
        //Test que solo se ejecutan dependiendo del sistema operativo
        @Test
        @EnabledOnOs(OS.WINDOWS)
        void testSoloWindows() {
            System.out.println("Ejecutado en windows");
        }

        @Test
        @EnabledOnOs({OS.LINUX, OS.MAC})
        void testSoloLinuxMax() {
            System.out.println("Ejecutado en Mac y linux");
        }

        @Test
        @DisabledOnOs(OS.WINDOWS)
        void testNoWindows() {
            System.out.println("Test que no se ejecuta en windows");
        }

        @Test
        @EnabledOnJre(JRE.JAVA_8)
        void testSoloJava8() {
            System.out.println("Solo se ejecurata si se usa Java 8");
        }

        //    Podemos ver todas las properties de nuestro sistema
        @Test
        void imprimirSystemProperties() {
            Properties properties = System.getProperties();
            properties.forEach((k, v) -> System.out.println(k + ":" + v));
        }

        @Test
        @EnabledIfSystemProperty(named = "java.version", matches = "15.0.1")
        void testJavaVersion() {

        }
    }

    @Nested
    class TestSobreElDev {
        //Test si existe una variable ambiente, se colora en edit configuration con -D(Nombre variable)
        @Test
        @EnabledIfSystemProperty(named = "ENV", matches = "dev")
        void testDev() {
        }

        @Test
        void imprimirVariablesAmbiente() {
            Map<String, String> getenv = System.getenv();
            getenv.forEach((k, v) -> System.out.println(k + " = " + v));
        }

        @Test
        @EnabledIfEnvironmentVariable(named = "NUMBER_OF_PROCESSORS", matches = "12")
        void testProcesadores() {

        }

        @Test
        @EnabledIfEnvironmentVariable(named = "ENVIROMENT", matches = "devs")
        void testEnv() {

        }

        //Este test solo se realizara si el servidor indicado esta usandose
        @Test
        @DisplayName("Probado si esta activo el server dev")
        void testSaldoCuentaDev() {
            boolean esDev = "dev".equals(System.getProperty("ENV")); //Solo se realizara si coinciden las propiedades indicadas
            assumeTrue(esDev); //Si es falso se deshabilita el test
            assertNotNull(cuenta.getSaldo());
            assertEquals(1000.12345, cuenta.getSaldo().doubleValue());
            assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }

        @Test
        @DisplayName("Probado si esta activo el server dev 2")
        void testSaldoCuentaDev2() {
            boolean esDev = "dev".equals(System.getProperty("ENV")); //Solo se realizara si coinciden las propiedades indicadas
            assumingThat(esDev, () -> {
                //Con esto dentro se coloca el codigo que queremos habilitar o deshabilitar
                assertNotNull(cuenta.getSaldo());
                assertEquals(1000.12345, cuenta.getSaldo().doubleValue());
                assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
                assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
            });
        }
    }

    //Para repetir un test varias veces
    @RepeatedTest(value=5, name="Repeticion numero {currentRepetition} de {totalRepetitions}")
    @DisplayName("Probando un test con repeticiones")
    void testDebitoCuentaRepetido(RepetitionInfo info){
        if(info.getCurrentRepetition() == 3){
            System.out.println("Estamos en la repeticion" + info.getCurrentRepetition());
        }

        cuenta.debito(new BigDecimal(100));
        assertNotNull(cuenta.getSaldo());
        assertEquals(900,cuenta.getSaldo().intValue());
        assertEquals("900.12345",cuenta.getSaldo().toPlainString());
    }
}