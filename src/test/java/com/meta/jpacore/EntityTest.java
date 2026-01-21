package com.meta.jpacore;


import com.meta.jpacore.entity.Memo;
import jakarta.persistence.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class EntityTest {
  EntityManagerFactory emf;
  EntityManager em;

  @BeforeEach
  void setUp() {
    emf = Persistence.createEntityManagerFactory("memo");
    em = emf.createEntityManager();
  }

  @Test
  @DisplayName("EntityTransaction 성공 테스트")
  void test1(){
    EntityTransaction et = em.getTransaction(); // EntityManager에서 Transaction 가져오기.
    et.begin(); // Transaction 시작

    try {
      // 저장할 엔터티 객체 생성
      Memo memo = new Memo();
//      memo.setId(1L); // id 식별자 값 detached entity passed to persist // id auto-increment와의 충돌 오류 해결
      memo.setUsername("김메타"); // 유저 이름
      memo.setContents("테스트코드로 넣는 메모 1"); // 내용 작성

      // 엔터티 매니저가 영속성 컨텍스트에 Entity 관리
      em.persist(memo);

      // 트랜잭션 커밋
      et.commit();
    } catch (Exception e){
        e.printStackTrace();
        // 실패시, 트랜잭션 롤백
        et.rollback();
    }finally {
      em.close();
    }

    emf.close();
  }

  @Test
  @DisplayName("EntityTransaction 실패 테스트")
  void test2(){
    EntityTransaction et = em.getTransaction(); // EntityManager에서 Transaction 가져오기.
    et.begin(); // Transaction 시작

    try {
      // 저장할 엔터티 객체 생성
      Memo memo = new Memo();
      memo.setUsername("김메타"); // 유저 이름
      memo.setContents("테스트코드로 넣는 메모 2"); // 내용 작성

      // 엔터티 매니저가 영속성 컨텍스트에 Entity 관리
      em.persist(memo);

      // 트랜잭션 커밋
      et.commit();
    } catch (Exception e){
      e.printStackTrace();
      // 실패시, 트랜잭션 롤백
      et.rollback();
    }finally {
      em.close();
    }

    emf.close();
  }

  @Test
  @DisplayName("1차 캐시 : ENtity 저장")
  void test3(){
    EntityTransaction et = em.getTransaction(); // EntityManager에서 Transaction 가져오기.
    et.begin(); // Transaction 시작

    try {
      Memo memo = new Memo();
      memo.setId(10L);
      memo.setUsername("김메타10"); // 유저 이름
      memo.setContents("1차 캐시 Entity 저장"); // 내용 작성

      em.persist(memo);

      // 트랜잭션 커밋
      et.commit();
    } catch (Exception e) {
      throw new RuntimeException(e);
    } finally {
      em.close();
    }
    emf.close();


  }

  @Test
  @DisplayName("1차 캐시 : 캐시 저장소에 해당하는 Id가 존재하지 않는 겨우")
  void test4(){
    try {
      Memo memo = em.find(Memo.class, 10L);
      System.out.println("memo.getId() = " + memo.getId());
      System.out.println("memo.getUsername() = " + memo.getUsername());
      System.out.println("memo.getContents() = " + memo.getContents());
    } catch (Exception e) {
      throw new RuntimeException(e);
    } finally {
      em.close();
    }
    emf.close();
  }
  @Test
  @DisplayName("1차 캐시 : 캐시 저장소에 해당하는 Id가 존재하지 하는 경우")
  void test5(){
    try {
      Memo memo1 = em.find(Memo.class, 10L);
      System.out.println("memo 10번 조회 후 캐시 저장소에 보관 됨");

      Memo memo2 = em.find(Memo.class, 10L);
      System.out.println("memo.getId() = " + memo2.getId());
      System.out.println("memo.getUsername() = " + memo2.getUsername());
      System.out.println("memo.getContents() = " + memo2.getContents());
    } catch (Exception e) {
      throw new RuntimeException(e);
    } finally {
      em.close();
    }
    emf.close();
  }

  @Test
  @DisplayName("객체 동일성 보장 확인")
  void test6(){
    EntityTransaction et = em.getTransaction();
    et.begin();

    try {
      Memo memo = new Memo();
      memo.setId(11L);
      memo.setUsername("김메타11");
      memo.setContents("객체 동일성 보장");
      em.persist(memo);

      Memo memo9 = em.find(Memo.class, 9L);
      Memo memo9_1 = em.find(Memo.class, 9L);
      Memo memo10 = em.find(Memo.class, 10L);

      System.out.println("memo9 == memo9_1 : " + (memo9 == memo9_1));
      System.out.println("memo9 == memo10 : " + (memo9 == memo10));
    } catch (Exception e) {
      throw new RuntimeException(e);
    } finally {
      em.close();
    }
    emf.close();

  }

  @Test
  @DisplayName("Entity 삭제")
  void test7(){
    EntityTransaction et = em.getTransaction();
    et.begin();

    try {
      Memo memo = em.find(Memo.class, 9L);
      em.remove(memo);

      et.commit();

    } catch (Exception e) {
      throw new RuntimeException(e);
    } finally {
      em.close();
    }
    emf.close();
  }


  @Test
  @DisplayName("쓰기 지연 저장소 Action Queue 확인")
  void test8(){
    EntityTransaction et = em.getTransaction();
    et.begin();

    try {
      Memo memo1 = new Memo();
      memo1.setId(10L);
      memo1.setUsername("김메타10");
      memo1.setContents("쓰기 지연 저장소 Action Queue 확인");
      em.persist(memo1);

      Memo memo2 = new Memo();
      memo2.setId(10L);
      memo2.setUsername("김메타11");
      memo2.setContents("저장을 잘하고 있을까?");
      em.persist(memo2);
      System.out.println("---트랜잭션 commit 전---");
      et.commit();
      System.out.println("---트랜잭션 commit 후---");


    } catch (Exception e) {
      throw new RuntimeException(e);
    } finally {
      em.close();
    }
    emf.close();
  }

  @Test
  @DisplayName("flush() 메서드 확인")
  void test9(){
    EntityTransaction et = em.getTransaction();
    et.begin();

    try {
      Memo memo1 = new Memo();
      memo1.setId(12L);
      memo1.setUsername("김메타12");
      memo1.setContents("flush() 메서드 확인");
      em.persist(memo1);

      System.out.println("--flush() 호출 전--");
      em.flush(); // flush 직접 호출
      System.out.println("--flush() 호출 후--");

      System.out.println("---트랜잭션 commit 전---");
      et.commit();
      System.out.println("---트랜잭션 commit 후---");

    } catch (Exception e) {
      throw new RuntimeException(e);
    } finally {
      em.close();
    }
    emf.close();
  }

  @Test
  @DisplayName("변경 감지 확인")
  void test10(){
    EntityTransaction et = em.getTransaction();
    et.begin();

    try {
      Memo memo = em.find(Memo.class, 10L);
      System.out.println("memo.getId() = " + memo.getId());
      System.out.println("memo.getUsername() = " + memo.getUsername());
      System.out.println("memo.getContents() = " + memo.getContents());

      System.out.println("\n메모 수정을 진행 합니다.");
      memo.setUsername("김수정");
      memo.setContents("변경 감지 확인");

      System.out.println("---트랜잭션 commit 전---");
      et.commit();
      System.out.println("---트랜잭션 commit 후---");

    } catch (Exception e) {
      throw new RuntimeException(e);
    } finally {
      em.close();
    }
    emf.close();
  }

  @Test
  @DisplayName("Entity의 상태 확인")
  void test11(){
    EntityTransaction et = em.getTransaction();
    et.begin();

    try {
      Memo memo = new Memo();  // 비영속 상태
      memo.setId(13L);
      memo.setUsername("김메타13");
      memo.setContents("Entity의 상태 확인");

      // 영속 상태
      em.persist(memo);
      Memo foundMemo = em.find(Memo.class, 13L);
      // em.contains(entity명) : Entity 객체가 현재 영속성 컨텍스트에 저장되어 관리되고 있는지 확인메서드
      System.out.println("em.contains(foundMemo) = " + em.contains(foundMemo));

      System.out.println("--- detach() 호출 전---");
      em.detach(foundMemo);
      System.out.println("---detach() 호출 후---");

      foundMemo.setUsername("김시도");
      foundMemo.setContents("준영속 상태의 엔터티 변경 시도");

      System.out.println("---트랜잭션 commit 전---");
      et.commit();
      System.out.println("---트랜잭션 commit 후---");

    } catch (Exception e) {
      throw new RuntimeException(e);
    } finally {
      em.close();
    }
    emf.close();
  }

  /*
  이외 확인 가능한 것
  - clear() : 영속성 컨텍스트 자체를 완전 초기화 하는 것
          -- 컨텍스트 내 모든 엔터티를 준영속(detach)상태로 전환하는 상태.

   - close() : 영속성 컨텍스트 종료
          -- clear와 유사하지만, 사용 불가
   - merge() : 준영속 또는 비영속 상태 엔터티를 영속상태로 다시 바꾸는 경우
   - remove() : 영속상태인 엔터티를 삭제 상태로 변환


   “JPA(EntityManager)가 DB랑 왔다갔다 할 때, 엔터티를 어떻게 관리하는지(=영속성 컨텍스트)
   직접 눈으로 확인하는 실험용 코드”
   */


}

