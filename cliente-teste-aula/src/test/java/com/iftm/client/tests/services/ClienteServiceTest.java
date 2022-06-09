package com.iftm.client.tests.services;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.iftm.client.dto.ClientDTO;
import com.iftm.client.entities.Client;
import com.iftm.client.repositories.ClientRepository;
import com.iftm.client.services.ClientService;
import com.iftm.client.services.exceptions.ResourceNotFoundException;

@ExtendWith(SpringExtension.class)
public class ClienteServiceTest {

		@InjectMocks
		private ClientService servico;
		@Mock
		private ClientRepository repositorio;

		@Test
		public void testRetornaVazioQuandoIdExiste() {
			Long idExistente = 2l;
			Mockito.doNothing().when(repositorio).deleteById(idExistente);

			Assertions.assertDoesNotThrow(() -> servico.delete(idExistente));
			Mockito.verify(repositorio, Mockito.times(1)).deleteById(idExistente);
		}

		public void testRetornaExceptionQuandoIdNaoExiste() {
			Long idNaoExistente = 1000l;
			Mockito.doThrow(ResourceNotFoundException.class).when(repositorio).deleteById(idNaoExistente);

			Assertions.assertThrows(ResourceNotFoundException.class, () -> servico.delete(idNaoExistente));
			Mockito.verify(repositorio, Mockito.times(1)).deleteById(idNaoExistente);
		}

		@Test
		public void testFindAllRetornaPaginaComTodosClientes() {
			PageRequest pageRequest = PageRequest.of(11, 1);
			List<Client> lista = new ArrayList<Client>();
			lista.add(new Client(12L, "'Jorge Amado", "10204374161", 2500.0, Instant.parse("1975-11-10T07:00:00Z"), 0));

			Page<Client> pag = new PageImpl<>(lista, pageRequest, lista.size());
			Mockito.when(repositorio.findAll(pageRequest)).thenReturn(pag);
			Page<ClientDTO> resultado = servico.findAllPaged(pageRequest);

			Assertions.assertFalse(resultado.isEmpty());
			Assertions.assertEquals(lista.size(), resultado.getNumberOfElements());
			for (int i = 0; i < lista.size(); i++) {
				Assertions.assertEquals(lista.get(i), resultado.toList().get(i).toEntity());
			}
			Mockito.verify(repositorio, Mockito.times(1)).findAll(pageRequest);
		}

		@Test
		public void testFindByIncome() {
			PageRequest pageRequest = PageRequest.of(0, 1, Direction.valueOf("ASC"), "name");
			Double entrada = 4500.00;
			List<Client> lista = new ArrayList<Client>();
			lista.add(new Client(8L, "Djamila Ribeiro", "10619244884", 4500.0, Instant.parse("1975-11-10T07:00:00Z"), 1));

			Page<Client> pag = new PageImpl<>(lista, pageRequest, lista.size());
			Mockito.when(repositorio.findByIncome(entrada, pageRequest)).thenReturn(pag);
			Page<ClientDTO> resultado = servico.findByIncome(pageRequest, entrada);
			Assertions.assertFalse(resultado.isEmpty());
			Assertions.assertEquals(lista.size(), resultado.getNumberOfElements());
			for (int i = 0; i < lista.size(); i++) {
				Assertions.assertEquals(lista.get(i), resultado.toList().get(i).toEntity());
			}
			Mockito.verify(repositorio, Mockito.times(1)).findByIncome(entrada, pageRequest);
		}

		@Test
		public void testFindByIdRetornaClientDtoQuandoIdExistir() {
			PageRequest pageRequest = PageRequest.of(0, 1);
			Long idExistente = 8l;
			Optional<Client> client = Optional.of(new Client(8L, "Djamila Ribeiro", "10619244884", 4500.0, Instant.parse("1975-11-10T07:00:00Z"), 1));
			Mockito.when(repositorio.findById(idExistente)).thenReturn(client);
			ClientDTO resultado = servico.findById(idExistente);
			Assertions.assertNotNull(resultado);
			Assertions.assertEquals(client.get(), resultado.toEntity());
			Mockito.verify(repositorio, Mockito.times(1)).findById(idExistente);
		}

		@Test
		public void testFindByIdLancaExceptionQuandoIdNaoExistir() {
			Long id = 1000l;
			Mockito.doThrow(ResourceNotFoundException.class).when(repositorio).findById(id);

			Assertions.assertThrows(ResourceNotFoundException.class, () -> servico.findById(id));
			Mockito.verify(repositorio, Mockito.times(1)).findById(id);
		}

		@Test
		public void testUpdateRetornaClientDtoQuandoExistirId() {
			//Cenario de teste
			Long id = 8l;
			Optional<Client> client = Optional.of(new Client());
			Client item = new Client(8L, "Djamila Ribeiro", "10619244884", 4500.0, Instant.parse("1975-11-10T07:00:00Z"), 1);
			Mockito.when(repositorio.getOne(id)).thenReturn(item);

			Client item2 = new Client(8L, "Djamila", "10619244884", 4500.0, Instant.parse("1975-11-10T07:00:00Z"), 1);
			Mockito.when(repositorio.save(item2)).thenReturn(item2);

			ClientDTO dto = servico.update(8L, new ClientDTO(item2));

			Assertions.assertEquals(item2, dto.toEntity());
			Mockito.verify(repositorio, Mockito.times(1)).getOne(id);
			Mockito.verify(repositorio, Mockito.times(1)).save(item2);
		}

		@Test
		public void testUpdateRetornaExceptionNaoExistirId() {
			Long idNaoExistente = 1000l;
			Optional<Client> client = Optional.of(new Client());
			Client item = new Client(8L, "Djamila Ribeiro", "10619244884", 4500.0, Instant.parse("1975-11-10T07:00:00Z"), 1);
			Mockito.doThrow(ResourceNotFoundException.class).when(repositorio).getOne(idNaoExistente);

			Client item2 = new Client(8L, "Djamila", "10619244884", 4500.0, Instant.parse("1975-11-10T07:00:00Z"), 1);
			Mockito.when(repositorio.save(item2)).thenReturn(item2);

			Assertions.assertThrows(ResourceNotFoundException.class, () -> servico.update(idNaoExistente, new ClientDTO(item2)));

			Mockito.verify(repositorio, Mockito.times(1)).getOne(idNaoExistente);
			Mockito.verify(repositorio, Mockito.times(0)).save(item2);
		}

		@Test
		public void testRetornarUmClientDTOInserirNovoCliente() {
			Long id = 8l;
			Optional<Client> client = Optional.of(new Client());
			Client item = new Client(8L, "Djamila Ribeiro", "10619244884", 4500.0, Instant.parse("1975-11-10T07:00:00Z"), 1);

			ClientDTO item2 = new ClientDTO(8L, "Djamila Ribeiro", "10619244884", 4500.0, Instant.parse("1975-11-10T07:00:00Z"), 1);
			Mockito.when(repositorio.save(item2.toEntity())).thenReturn(item2.toEntity());
			ClientDTO dto = servico.insert(item2);


			Assertions.assertEquals(dto.toEntity(), item);
			Mockito.verify(repositorio, Mockito.times(1)).save(dto.toEntity());
		}
	}