package org.olf

import grails.gorm.multitenancy.Tenants
import org.olf.kb.RemoteKB
import grails.events.annotation.Subscriber
import grails.gorm.multitenancy.WithoutTenant
import grails.gorm.transactions.Transactional
import org.olf.kb.Package;
import org.olf.kb.TitleInstance

/**
 * This service works at the module level, it's often called without a tenant context.
 */
@Transactional
public class TitleInstanceResolverService {


  /**
   * Given a -valid- title citation with the minimum properties below, attempt to resolve the citation
   * into a local instance record. If no instance record is located, create one, and perform the necessary
   * cross-matching to create Inventory Instance records. The map contains a representation that is
   * the same as the attached JSON.
   *
   * {
   *   "title": "Nordic Psychology",
   *   "instanceMedium": "electronic",
   *   "instanceMedia": "journal",
   *   "instanceIdentifiers": [ 
   *     {
   *       "namespace": "eissn",
   *       "value": "1234-5678"
   *     } ],
   *   "siblingInstanceIdentifiers": [ 
   *     {
   *       "namespace": "issn",
   *       "value": "1901-2276"
   *     } ],
   *   "coverage": [ {
   *     "startVolume": "58",
   *     "startIssue": "1",
   *     "startDate": "2006-03-31T23:00:00Z",
   *     "endVolume": "63",
   *     "endIssue": "4",
   *     "endDate": "2011-12-31T00:00:00Z"
   *   } ],
   *   "embargo": null,
   *   "coverageDepth": "fulltext",
   *   "coverageNote": null
   *   }
   */
  public TitleInstance resolve(Map citation) {
    TitleInstance result = null;
    return result;
  }  
}
