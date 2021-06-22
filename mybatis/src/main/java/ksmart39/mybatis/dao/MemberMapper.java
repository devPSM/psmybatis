package ksmart39.mybatis.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import ksmart39.mybatis.domain.Member;

@Mapper
public interface MemberMapper {
	
	//회원 목록 조회
	public List<Member> getMemberList(Map<String, Object> paramMap);
	
	//회원 가입
	public int addMember(Member member);
	
	//회원 정보 수정
	public int modifyMember(Member member);
	
	//한명 회원 조회
	public Member getOneMember(String memberId);
	
	//회원 권한 조회
	public int memberLevel(String memberId);

	//회원 테이블 정보 삭제
	public int removeMemberById(String memberId);
	
	//상품 테이블 삭제
	public int removeGoodsById(String memberId);
	
	//로그인 테이블 삭제
	public int removeLoginById(String memberId);

	//주문 테이블(구매자) 삭제
	public int removeOrderById(String memberId);
	
	//주문 테이블(판매자) 삭제
	public int removeOrderBySellerId(String memberId);
}
