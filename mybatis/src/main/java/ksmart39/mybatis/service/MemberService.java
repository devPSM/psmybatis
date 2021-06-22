package ksmart39.mybatis.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ksmart39.mybatis.dao.MemberMapper;
import ksmart39.mybatis.domain.Member;

@Service
@Transactional
public class MemberService {
	
	private static final Logger log = LoggerFactory.getLogger(MemberService.class);
	
	/**
	 * 원래는 MemberMapper memberMapper = new MemberMapper(); 
	 * setter 메서드 활용해서 memberMapper
	 * 생성자메서드를 사용해서 memberMapper
	 * 필드 주입방식 (DI)으로 하면 밑에처럼 작성해주면 생성했던 객체를 자동으로 빌드해줌
	 **/
	@Autowired
	private MemberMapper memberMapper;
	
	@PostConstruct
	public void MemberServiceInit() {
		log.info("(´▽`ʃ♡ƪ)  [ MemberService 객체 생성 ] (´▽`ʃ♡ƪ)");
	}
	
	//로그인 체크
	public Map<String, Object> loginMember(String memberId, String memberPw) {
		//로그인 여부
		boolean loginCheck = false;
		
		//로그인 결과
		Map<String, Object> memberInfoMap = new HashMap<String, Object>();
		
		//로그인 처리
		Member member = memberMapper.getOneMember(memberId);
		
		if(member != null && memberPw.equals(member.getMemberPw())) {
			loginCheck = true;
			memberInfoMap.put("loginMember", member);
		}
		
		memberInfoMap.put("loginCheck", loginCheck);
		
		return memberInfoMap;
	}
	
	// 회원 목록 조회
	public List<Member> getMemberList(Map<String, Object> paramMap){
		List<Member> memberList = memberMapper.getMemberList(paramMap);
		return memberList;
	}
	
	// 회원 가입
	public int addMember(Member member) {
		int result = memberMapper.addMember(member);
		return result;
	}
	
	// 한명 회원 조회
	public Member getOneMember(String memberId) {
		return memberMapper.getOneMember(memberId);
	}
	
	// 회원 정보 수정
	public int modifyMember(Member member) {
		return memberMapper.modifyMember(member);
	}
	
	// 회원 정보 삭제 (끝 부분부터 삭제) 총 4개 테이블 지울수있는 DELETE쿼리 필요
	public boolean removeMember(String memberId, String memberPw) {
		//삭제여부 Boolean
		boolean removeCheck = false;
		
		//비밀번호 맞으면 삭제
		Member member = memberMapper.getOneMember(memberId);
		if(member != null && memberPw.equals(member.getMemberPw())) {
			//삭제프로세스
			
			//판매자 - 주문, 상품
			if("2".equals(member.getMemberLevel())) {
				memberMapper.removeOrderBySellerId(memberId);
				memberMapper.removeGoodsById(memberId);
			}
			
			//구매자 - 주문, 상품
			if("3".equals(member.getMemberLevel())) {
				memberMapper.removeOrderById(memberId);
			}
			//공통부분 - 로그인, 회원
			memberMapper.removeLoginById(memberId);
			memberMapper.removeMemberById(memberId);
			
			//삭제프레세스 끝 - 삭제여부 변경 후 리턴
			removeCheck = true;
		}
		return removeCheck;
	}
}














