//package fr.labri.progress.comet.endpoint;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import javax.inject.Inject;
//import javax.ws.rs.Consumes;
//import javax.ws.rs.GET;
//import javax.ws.rs.POST;
//import javax.ws.rs.Path;
//import javax.ws.rs.Produces;
//import javax.ws.rs.core.MediaType;
//import javax.ws.rs.core.Response;
//
//import fr.labri.progress.comet.model.ThirdPartyConfiguration;
//import fr.labri.progress.comet.model.jaxb.ThirdPartyStorage;
//import fr.labri.progress.comet.repository.ThirdPartyStorageConfigRepository;
//import fr.labri.progress.comet.service.ThridPartyStorageService;
//
//
//@Path("thirdpartystorage")
//public class ThridPartyStorageEndPoint {
//
//	@Inject
//	ThridPartyStorageService tpsService;
//
//	@Inject
//	ThirdPartyStorageConfigRepository repo;
//
//	@GET
//	@Produces("application/json")
//	public List<ThirdPartyStorage> getTPS() {
//		List<ThirdPartyStorage> res = new ArrayList<ThirdPartyStorage>();
//		for (ThirdPartyConfiguration conf : repo.findAll()) {
//			ThirdPartyStorage tps = new ThirdPartyStorage();
//			tps.setId("" + conf.getId());
//			tps.setName(conf.getName());
//			tps.setUrl(conf.getBaseUrl());
//			res.add(tps);
//		}
//
//		return res;
//
//	}
//
//	@POST
//	@Consumes(MediaType.APPLICATION_JSON)
//	public Response registerTPS(ThirdPartyStorage tps) {
//
//		tpsService.register(tps.getUrl(), tps.getName());
//		return Response.noContent().build();
//	}
//
//}