package com.dxsys.graphql.springgraphql.controler;

import com.dxsys.graphql.springgraphql.dao.PersonRepository;
import com.dxsys.graphql.springgraphql.entity.Person;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.schema.DataFetcher;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.util.List;

@RestController
public class PersonController {

    @Autowired
    private PersonRepository personRepository;

    @Value("classpath:person.graphqls")
    private Resource schemaResource;

    private GraphQL graphQL;

    @PostConstruct
    public void loadSchema() throws IOException {
        File schemaFile = schemaResource.getFile();
        TypeDefinitionRegistry registry = new SchemaParser().parse(schemaFile);
        RuntimeWiring wiring = buildWiring();
        GraphQLSchema schema = new SchemaGenerator().makeExecutableSchema(registry,wiring);
        graphQL = GraphQL.newGraphQL(schema).build();
    }

    public RuntimeWiring  buildWiring() {
        DataFetcher<List<Person>> fecther1 = data-> (List<Person>) personRepository.findAll();

        DataFetcher<Person> fecther2 = data-> personRepository.findByEmail(data.getArgument("email"));

        return RuntimeWiring.newRuntimeWiring().type("Query",
                typeWiring-> typeWiring.dataFetcher("getALLPerson",fecther1).dataFetcher("findPerson", fecther2))
                        .build();

    }
    @PostMapping("/addPerson")
    public String addPerson(@RequestBody List<Person> persons) {
            personRepository.saveAll(persons);
        return "record inserted " + persons.size();
    }

    @GetMapping("/findAllPersons")
    public List<Person> getPersons() {
        return (List<Person>) personRepository.findAll();
    }

    @PostMapping("/getAll")
    public ResponseEntity<Object> findAllPerson(@RequestBody String query) {
        ExecutionResult result = graphQL.execute(query);
        return new ResponseEntity<Object>(result, HttpStatus.OK);
    }

    @PostMapping("/getPersonByEmail")
    public ResponseEntity<Object> getPersonByEmail(@RequestBody String query) {
        ExecutionResult result = graphQL.execute(query);
        return new ResponseEntity<Object>(result, HttpStatus.OK);
    }

    @GetMapping("/healthcheck")
    public ResponseEntity<String> healthcheck(){
        return new ResponseEntity<String>("Service is up and running", HttpStatus.OK);
    }

}
