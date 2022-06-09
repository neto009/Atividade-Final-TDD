package com.iftm.client.tests.services;

import com.iftm.client.dto.ClientDTO;
import com.iftm.client.entities.Client;
import com.iftm.client.services.ClientService;
import com.iftm.client.services.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;

import javax.transaction.Transactional;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@SpringBootTest
@Transactional
public class ClienteServiceTestIT {

		@Autowired
		private ClientService servico;

		@Test
		public void testRetornaVazioQuandoIdExiste() {
			Long idExistente = 2l;

			Assertions.assertDoesNotThrow(() -> servico.delete(idExistente));
		}

		public void testRetornaExceptionQuandoIdNaoExiste() {
			Long idNaoExistente = 1000l;

			Assertions.assertThrows(ResourceNotFoundException.class, () -> servico.delete(idNaoExistente));
		}

		@Test
		public void testFindAllRetornaPaginaComTodosClientes() {
			PageRequest pageRequest = PageRequest.of(11, 1);
			List<Client> lista = new ArrayList<Client>();
			lista.add(new Client(12L, "'Jorge Amado", "10204374161", 2500.0, Instant.parse("1975-11-10T07:00:00Z"), 0));

			Page<Client> pag = new PageImpl<>(lista, pageRequest, lista.size());
			Page<ClientDTO> resultado = servico.findAllPaged(pageRequest);

			Assertions.assertFalse(resultado.isEmpty());
			Assertions.assertEquals(lista.size(), resultado.getNumberOfElements());
			for (int i = 0; i < lista.size(); i++) {
				Assertions.assertEquals(lista.get(i), resultado.toList().get(i).toEntity());
			}
		}

		@Test
		public void testFindByIncome() {
			PageRequest pageRequest = PageRequest.of(0, 1, Direction.valueOf("ASC"), "name");
			Double entrada = 4500.00;
			List<Client> lista = new ArrayList<Client>();
			lista.add(new Client(6L, "Djamila Ribeiro", "10619244884", 4500.0, Instant.parse("1975-11-10T07:00:00Z"), 1));

			Page<Client> pag = new PageImpl<>(lista, pageRequest, lista.size());
			Page<ClientDTO> resultado = servico.findByIncome(pageRequest, entrada);
			Assertions.assertFalse(resultado.isEmpty());
			Assertions.assertEquals(lista.size(), resultado.getNumberOfElements());
			for (int i = 0; i < lista.size(); i++) {
				System.out.println( resultado.toList().get(i).toEntity().getName());
				System.out.println(lista.get(i).getName());
				Assertions.assertEquals(lista.get(i), resultado.toList().get(i).toEntity());

			}
		}

		@Test
		public void testFindByIdRetornaClientDtoQuandoIdExistir() {
			PageRequest pageRequest = PageRequest.of(0, 1);
			Long idExistente = 8l;
			Optional<Client> client = Optional.of(new Client(8L, "Djamila Ribeiro", "10619244884", 4500.0, Instant.parse("1975-11-10T07:00:00Z"), 1));
			ClientDTO resultado = servico.findById(idExistente);
			Assertions.assertNotNull(resultado);
			Assertions.assertEquals(client.get(), resultado.toEntity());
		}

		@Test
		public void testFindByIdLancaExceptionQuandoIdNaoExistir() {
			Long id = 1000l;

			Assertions.assertThrows(ResourceNotFoundException.class, () -> servico.findById(id));
		}

		@Test
		public void testUpdateRetornaClientDtoQuandoExistirId() {
			//Cenario de teste
			Long id = 8l;
			Optional<Client> client = Optional.of(new Client());
			Client item = new Client(8L, "Djamila Ribeiro", "10619244884", 4500.0, Instant.parse("1975-11-10T07:00:00Z"), 1);

			Client item2 = new Client(8L, "Djamila", "10619244884", 4500.0, Instant.parse("1975-11-10T07:00:00Z"), 1);

			ClientDTO dto = servico.update(8L, new ClientDTO(item2));

			Assertions.assertEquals(item2, dto.toEntity());
		}

		@Test
		public void testUpdateRetornaExceptionNaoExistirId() {
			Long idNaoExistente = 1000l;
			Optional<Client> client = Optional.of(new Client());
			Client item = new Client(8L, "Djamila Ribeiro", "10619244884", 4500.0, Instant.parse("1975-11-10T07:00:00Z"), 1);

			Client item2 = new Client(8L, "Djamila", "10619244884", 4500.0, Instant.parse("1975-11-10T07:00:00Z"), 1);

			Assertions.assertThrows(ResourceNotFoundException.class, () -> servico.update(idNaoExistente, new ClientDTO(item2)));
		}

		@Test
		public void testRetornarUmClientDTOInserirNovoCliente() {
			Long id = 8l;
			Optional<Client> client = Optional.of(new Client());
			Client item = new Client(8L, "Djamila Ribeiro", "10619244884", 4500.0, Instant.parse("1975-11-10T07:00:00Z"), 1);

			ClientDTO item2 = new ClientDTO(8L, "Djamila Ribeiro", "10619244884", 4500.0, Instant.parse("1975-11-10T07:00:00Z"), 1);

			ClientDTO dto = servico.insert(item2);


			Assertions.assertEquals(dto.toEntity(), item);
		}


	}