
package repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import domain.Actor;
import domain.Box;
import domain.Message;

@Repository
public interface MessageRepository extends JpaRepository<Message, Integer> {

	@Query("select m from Message m")
	public List<Message> findAll2();

	@Query("select b.messages from Box b where b = ?1")
	public List<Message> getMessagesOfBox(Box box);

	@Query("select m from Actor a join a.boxes b join b.messages m where a = ?1")
	public List<Message> messagesOfActor(Actor a);

}
