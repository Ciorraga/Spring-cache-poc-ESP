# POC SPRING CACHE
## Spring Cache

En esta POC se va a abordar la implementación de la caché de Spring dentro de los servicios ya existentes. También se adjunta  en este repositorio un proyecto creado a modo de ejemplo en el cual se puede hacer una prueba completa de esta funcionalidad.

Para esta POC, **se adjunta una base de datos de postgres** en la cuál ya hay datos suficientes para poder realizar test de cachés. **Esta base de datos viene incorporada en un archivo docker-compose**.

También se agrega openApi en esta poc con lo que, una vez arrancado el microservicio, si entramos en la URL **http://localhost:8080/swagger-ui.html** podremos ejecutar nuestros endpoints sin mayor problema.

**FALTA ADJUNTAR LOS ARCHIVOS DE CARGA DE DATOS**

Todo lo explicado a continuación se puede encontrar de manera ya práctica en este repositorio.

## Dependencia a añadir

```
<!-- pom.xml -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-cache</artifactId>
</dependency>
```


## Anotaciones a incluir en NOMBREPROYECTOApplication.java

```
@SpringBootApplication
@EnableCaching
@EnableScheduling
public class PocSpringCacheApplication {

	public static void main(String[] args) {
		SpringApplication.run(PocSpringCacheApplication.class, args);
	}

}
```

## Configuración básica

Para poder implantar la caché, en primer lugar habrá que crear un archivo de configuración que será encargado de setear las cachés que vamos a utilizar. 

Para, por ejemplo, un service básico encargado de hacer un CRUD básico sobre una entidad llamada **Store**, el fichero de configuración será el siguiente:

```
@Configuration
@EnableCaching
public class CacheConfig {

  @Bean
  public CacheManager cacheManager() {
    ConcurrentMapCacheManager mgr = new ConcurrentMapCacheManager();
    mgr.setCacheNames(Arrays.asList("stores", "cities"));
    return mgr;
  }
}
```

El fichero como tal de configuración, para seguir una nomenclatura común SIEMPRE se va a llamar **CacheConfig**.

## Añadiendo caché a metodos de ServiceImpl

Comunmente, las cachés se van a tener que implantar en los métodos que nos devuelvan un listado de datos en el que la carga puede tardar demasiado, o también en métodos que se llaman muy frecuentemente con lo que, de esta manera, agilizamos bastante los tiempos de carga que van a tener nuestros endpoints.

En primer lugar, basándonos en esta POC, lo que vamos a detallar a continuación se va a hacer sobre un ServiceImpl que, para una entidad llamada Store, se va a encargar de hacer un CRUD básico sobre la misma.

### Listado general de datos

- Para el método encargado de devolver un listado, lo marcamos como Cacheable(cacheNames = "stores"), y así, automáticamente Spring seteará los datos devueltos en la caché que nombramos y hemos agregado a la configuración previamente.

    ```
    @Override
	@Cacheable(cacheNames = "stores") //Marcamos el método como Cacheable. Así guardará los datos devueltos en la caché "stores".
	public List<Store> getStores() {
		try {            
            Thread.sleep(3000);
            log.info("return value after {} seconds", 3);
            
            List<Store> store = storeRepository.findAll();
    		
    		return store;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
	}
    ```

### Listados filtrando por parámetros

- Para guardar en caché los datos devueltos en un endpoint de consulta el cuál realiza una llamada filtrada a base de datos (getById, findById...etc) la caché se setea de la siguiente manera:

    ```
    @Override
	@Cacheable(cacheNames = "stores", key="#id") //Seteamos la caché basándonos en el ID que nos ha venido informado por parámetro.
	public Store getStore(Integer id) {
		try {
        	Thread.sleep(3000);
            log.info("return value after {} seconds", 3);
            
            return storeRepository.findById(id)
            		.orElse(null);

        } catch (Exception e) {
        	log.error(e.getMessage());
            throw new RuntimeException(e);
        }
	}
    ``` 
- Otra manera de realizar la llamada a un listado general es **obtener todo el listado de datos** y luego filtrar o paginar sobre él.

    ```
 	@GetMapping("/stores")
	public ResponseEntity<Page<Store>> getAllStores(
			@RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size) {
		
		List<Store> stores = storeService.getStores();
		
		// Paginacion de lo cacheado
		Pageable pageable = PageRequest.of(page, size);
		Page<Store> pageStores = (Page<Store>) PaginationUtil.toPage(stores, pageable);
				
		return !stores.isEmpty() 
				? new ResponseEntity<>(pageStores, HttpStatus.OK) 
				: new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
    ```

    **El método toPage se puede ver en el paquete util, clase PaginationUtil**

### Listados Borrado de caché en inserts, updates, deletes

- Cuando cacheamos datos en nuestros servicios, es **EXTREMADAMENTE IMPORTANTE** que, cuando realizamos un guardado, un borrado o una modificación en los datos que tenemos alojados en nuestra base de datos, la caché se borre para que así en las siguiente consultas los datos devueltos no sean erróneos.

    ```
    @Override
	@CacheEvict(cacheNames = "stores", allEntries = true)
	public Store updateStore(Integer id, Store newStore) {
		try {
        	Thread.sleep(3000);
            log.info("return value after {} seconds", 3);
            
            return storeRepository.save(newStore);
        } catch (Exception e) {
        	log.error(e.getMessage());
            throw new RuntimeException(e);
        }		
	}

	@Override
	@CacheEvict(cacheNames = "stores", allEntries = true)
	public Store saveStore(Store newStore) {
		try {
        	Thread.sleep(3000);
            log.info("return value after {} seconds", 3);
            
            return storeRepository.save(newStore);
        } catch (Exception e) {
        	log.error(e.getMessage());
            throw new RuntimeException(e);
        }			
	}

    @Override
	@CacheEvict(cacheNames = "stores", allEntries = true)
	public void deleteStore(Integer id) {
		try {
        	Thread.sleep(3000);
            log.info("return value after {} seconds", 3);
            
            storeRepository.deleteById(id);
        } catch (Exception e) {
        	log.error(e.getMessage());
            throw new RuntimeException(e);
        }		
	}
    ```

### --OPCIONAL-- Nombre general de caché por clase

- Si no queremos estar nombrando la caché en cada uno de los métodos del service, podemos nombrarla de manera generalizada a nivel de clase.


    ```
    @CacheConfig(cacheNames = "stores") //Aquí damos nombre a la caché que va a ser utilizada en esta clase.
    public class StoreServiceImpl implements StoreService {

	@Autowired
	private StoreRepository storeRepository;
	. 
    .
    .
    }
    ```    

    De hacerlo de este modo, en los métodos del serviceImpl no tendremos que nombrar la caché. Tendríamos que eliminar, en nuestro ejemplo, cacheNames = "stores". A continuación se muestra un ejemplo:

    ```
    @Override
	@Cacheable() //Marcamos el método como Cacheable. Así guardará los datos devueltos en la caché "stores" que ha sido seteada en la anotación de clase.
	public List<Store> getStores() {		
		try {            
            Thread.sleep(3000);
            log.info("return value after {} seconds", 3);
            
            List<Store> store = storeRepository.findAll();
    		
    		return store;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
	}
    ```

## Borrado automático de la caché

**IMPORTANTE**

Para hacer una buena gestión de la caché de los datos, y para sobretodo no sobrecargar nuestro microservicio, cada caché que implementemos debería tener el método que se detallará a continuación el cuál se encargará de borrar una o varias cachés pasado un tiempo. Este tiempo es seteado en MILISEGUNDOS. Con lo que, si por ejemplo, queremos que la caché se limpia tras 10 minutos, el valor que tendríamos que asignar sería "60000" tal y como se muestra a continuación:


```
	@CacheEvict(cacheNames = "stores", allEntries = true)
	@Scheduled(fixedRateString = "60000")
	public void emptyCache() {
	    log.info("emptying cache"); //Ojo con este log info. Si ponemos un tiempo muy pequeño vamos a sobrecargar el log. Si es poco tiempo de caché log.debug
	}
```

## Borrado de múltiples cachés.

En ciertos casos, como por ejemplo en el de esta POC, tenemos una entidad Store, la cual en su interior tiene una entidad City. Cuando cacheamos, un fallo muy común se da cuando cacheamos el elemento Store, modificamos el elemento City, al estar la entidad Store cacheada, no se va a actualizar y los datos mostrados van a inducir a error.

Es decir, si suponemos que Store está cacheado, y City también, al usarse City dentro de store, cuando lo modifiquemos, demos de alta un registro, o borremos, debería hacer lo siguiente:

```
	@Override
	@Caching(evict = {
	    @CacheEvict(cacheNames = "cities", allEntries = true), 
	    @CacheEvict(cacheNames = "stores", allEntries = true)})
	public void deleteCity(Integer id) {
		try {
        	Thread.sleep(3000);
            log.info("return value after {} seconds", 3);
            
            CityRepository.deleteById(id);
        } catch (Exception e) {
        	log.error(e.getMessage());
            throw new RuntimeException(e);
        }		
	}
```



