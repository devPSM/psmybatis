package ksmart39.mybatis.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import ksmart39.mybatis.domain.Member;
import ksmart39.mybatis.service.GoodsService;
import ksmart39.mybatis.service.MemberService;

@Controller
public class MemberController {
	
	/**
	 * 원래는 MemberService memberService = new MemberService(); 
	 * setter 	메서드 주입방식이 있고 GoodsService
	 * 생성자		메서드를 주입방식이 있고 GoodsService
	 * 필드 주입방식 (DI)으로 하면 밑에처럼 작성해주면 생성했던 객체를 자동으로 빌드해줌
	 **/
	
	
	/**
	 * 필드 주입방식
	 * @Autowired
	 * private MemberService memberService;
	 * @Autowired
	 * private GoodsService goodsService;
	 */
	
	/**
	* setter 메서드 주입방식
	* private MemberService memberService;
	* private GoodsService goodsService;
	*
	* @Autowired
	* public void setMemberService(MemberService memberService, GoodsService goodsService) {
	*	 this.memberService = memberService;
	*	 this.goodsService = goodsService;
	* }
	*/
	
	
	
	private static final Logger log = LoggerFactory.getLogger(MemberController.class);
	
	//생성자 메서드 주입방식 (순환참조가 안일어남)  ★요즘 추세임 		------final로 꼭 해야한다.
	private final MemberService memberService;
	//private final GoodsService goodsService;
	
	//spring framework 4.3이후부터 @Autowired 생략가능
	@Autowired
	public MemberController(MemberService memberService, GoodsService goodsService) {
		this.memberService = memberService;
		//this.goodsService = goodsService;
	}
	
	@PostConstruct
	public void memberControllerInit() {
		log.info("༼ つ ◕_◕ ༽つ     [ memberController 객체 생성 ] ༼ つ ◕_◕ ༽つ");
	}
/*------------------------------------------------------------------------------------------------------------------로그아웃--------------------------------------------------------------------------------------*/
	@GetMapping("/logout")
	public String logout(HttpSession session) {
		session.invalidate();
		return "redirect:/";
	}
/*------------------------------------------------------------------------------------------------------------------로그인--------------------------------------------------------------------------------------*/
	/* HttpServletRequest request -> request.getSession
	 * 어차피 session을 요청하는거면 더 쉽게 바로 접근하자
	 * HttpSession session
	 * */
	@PostMapping("/login")
	public String login(@RequestParam(value = "memberId", required = false) String memberId
					  , @RequestParam(value = "memberPw", required = false) String memberPw
					  , HttpSession session
					  , RedirectAttributes reAttr) {
		
		if(memberId != null && !"".equals(memberId) && memberPw != null && !"".equals(memberPw)) {
			Map<String, Object> resultMap = memberService.loginMember(memberId, memberPw);
			
			// Object 로 오기 때문에 다운케스트 해줘야함
			boolean loginCheck = (boolean) resultMap.get("loginCheck");
			Member loginMember = (Member) resultMap.get("loginMember");
			
			if(loginCheck) {
				session.setAttribute("SID", 		loginMember.getMemberId());
				session.setAttribute("SNAME", 		loginMember.getMemberName());
				session.setAttribute("SLEVEL", 		loginMember.getMemberLevel());
				if(loginMember.getMemberLevel().equals("1")) {
					session.setAttribute("SLEVELNAME", "관리자");
				}else if(loginMember.getMemberLevel().equals("2")) {
					session.setAttribute("SLEVELNAME", "판매자");
				}else if(loginMember.getMemberLevel().equals("3")) {
					session.setAttribute("SLEVELNAME", "구매자");
				}
				return "redirect:/";
			}
		}
		//login?loginResult=등록된 회원이 없습니다. 와 같은방식으로 넘어감 requestAttributes 로 해야 자동 인코딩이 됨.
		reAttr.addAttribute("loginResult", "등록된 회원이 없습니다.");
		
		return "redirect:/login";
	}
	
	@GetMapping("/login")
	public String login(Model model, @RequestParam(name = "loginResult", required = false) String loginResult) {
		
		model.addAttribute("title", "로그인화면");
		if(loginResult != null) model.addAttribute("loginResult", loginResult);
		
		return "login/login";
	}
/*-----------------------------------------------------------------------------------------------------------------회원 삭제------------------------------------------------------------------------------------*/
	@PostMapping("/removeMember")
	public String removeMember(@RequestParam(value = "memberId", required = false) String memberId
							  ,@RequestParam(value = "memberPw", required = false) String memberPw
							  ,RedirectAttributes redirectAttr) {
		log.info("회원탈퇴 화면에서 입력받은 아이디 : {}", memberId);
		log.info("회원탈퇴 화면에서 입력받은 패스워드 : {}", memberPw);
		
		if(memberPw != null && !"".equals(memberPw)) {
			boolean result = memberService.removeMember(memberId, memberPw);
			if(result) {
				return "redirect:/memberList";
			}
		}

		redirectAttr.addAttribute("memberId", memberId);
		redirectAttr.addAttribute("result", "삭제 실패");
		
		return "redirect:/removeMember";
	}
	
	@GetMapping("/removeMember")
	public String removeMember(@RequestParam(value = "memberId", required = false) String memberId
							  ,@RequestParam(value = "result", required = false) String result
							  ,Model model) {
		
		log.info("┍━━━━━━━━━━━━━━━━━━━━━━━━━»•» 🌸 «•«━━━━━━┑");
		log.info("    ❗  리스트화면에서 입력받은 값(회원 삭제 폼) : {}", memberId , " ❗");
		log.info("┕━━━━━━»•» 🌸 «•«━━━━━━━━━━━━━━━━━━━━━━━━━┙");
		
		model.addAttribute("title", "회원탈퇴폼");
		model.addAttribute("memberId", memberId);
		
		if(result != null) {
			model.addAttribute("result", result);
		}

		return "member/removeMember";
	}
/*-----------------------------------------------------------------------------------------------------------------회원 수정-----------------------------------------------------------------------------------*/
	@GetMapping("/modifyMember")
	public ModelAndView modifyMember(@RequestParam(name = "memberId", required = false) String memberId
			,Model model) {
		ModelAndView mav = new ModelAndView();
		//1. 회원 아이디로 회원테이블 정보를 조회한 Member 객체
		Member member = memberService.getOneMember(memberId);
		
		log.info("┍━━━━━━━━━━━━━━━━━━━━━━━━━»•» 🌸 «•«━━━━━━┑");
		log.info("      수정을 원하는 회원아이디(회원 수정 폼) : {}", memberId);
		log.info("┕━━━━━━»•» 🌸 «•«━━━━━━━━━━━━━━━━━━━━━━━━━┙");
		
		//2. Model 화면에 전달할 객체를 삽입
		mav.addObject("title", "수정화면폼");
		mav.addObject("member", member);
		mav.setViewName("member/modifyMember");
		return mav;
	}
	
	@PostMapping("/modifyMember")
	public String modifyMember(Member member) {
		
		log.info("┍━━━━━━━━━━━━━━━━━━━━━━━━━»•» 🌸 «•«━━━━━━┑");
		log.info("      수정완료 후 입력받은 값(회원 수정 폼) : {}", member);
		log.info("┕━━━━━━»•» 🌸 «•«━━━━━━━━━━━━━━━━━━━━━━━━━┙");
		
		memberService.modifyMember(member);
		return "redirect:/memberList";
	}
	
	/**
	 * [@RequestParam(name = "memberId", required = false)]
	 * 위 내용은 이것과 같다. request.getParameter("memberId");
	 * [String memberId = ]에 해당하는건 두번째 인자값.
	 */
	
/*-----------------------------------------------------------------------------------------------------------------회원 가입-----------------------------------------------------------------------------------*/
	
	/**
	 * @param memberId, memberPw, ...... => Member dto 의 멤버변수와 이름이 같다면 스프링이 알아서 바인딩해준다.
	 * 		    커멘드객체(Member)
	 **/
	@PostMapping("/addMember")
	public String addMember(Member member) {
		
		log.info("╭┈😆");
		log.info("│     ✐ ;  화면에서 입력 받은 값(회원가입) ");
		log.info("│     ✐ ; {}",member);
		log.info("╰─────────────────👍⠀⠀");
		
		memberService.addMember(member);
		
		return "redirect:/memberList";
	}
	
	@GetMapping("/addMember")
	public String addMember(Model model) {
		
		model.addAttribute("title", "회원가입폼");
		return "member/addMember";
	}
	
/*-----------------------------------------------------------------------------------------------------------------전체회원조회-----------------------------------------------------------------------------------*/	
	@GetMapping("/memberList")
	public String getMemberList(Model model
							  ,@RequestParam(name = "searchKey", required = false)String searchKey
							  ,@RequestParam(name = "searchValue", required = false)String searchValue) {
		
		log.info("╭┈🌙");
		log.info("│     ✐ ; 입력 받은 값 ");
		log.info("│     ✐ ; searchKey : {}", searchKey);
		log.info("│     ✐ ; searchValue : {}", searchValue);
		log.info("╰─────────────────🌟⠀⠀");
		
		//map을 활용해서 검색 키워드 정리  type은 전체를 받게 하려면 String, Object를 한다.
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("searchKey", searchKey);
		paramMap.put("searchValue", searchValue);
		
		List<Member> memberList = memberService.getMemberList(paramMap);
		log.info("memberList 사이즈"+memberList.size());
		
		log.info("╭┈🌙");
		log.info("│     ✐ ;  화면에서 입력 받은 값(회원조회) ");
		log.info("│     ✐ ; memberList : {}", memberList);
		log.info("╰─────────────────🌟⠀⠀");
		
		model.addAttribute("title", "회원목록");
		model.addAttribute("memberList", memberList);
		
		return "member/memberList";
	}
	
	
}
