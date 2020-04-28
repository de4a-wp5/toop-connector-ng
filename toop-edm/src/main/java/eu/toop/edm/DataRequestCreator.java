package eu.toop.edm;

import java.time.LocalDateTime;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.CommonsLinkedHashMap;
import com.helger.commons.collection.impl.CommonsLinkedHashSet;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.collection.impl.ICommonsOrderedMap;
import com.helger.commons.collection.impl.ICommonsOrderedSet;
import com.helger.commons.datetime.PDTFactory;

import eu.toop.edm.jaxb.cccev.CCCEVRequirementType;
import eu.toop.edm.jaxb.cv.agent.AgentType;
import eu.toop.edm.jaxb.w3.cv.ac.CoreBusinessType;
import eu.toop.edm.jaxb.w3.cv.ac.CorePersonType;
import eu.toop.edm.regrep.ISlotProvider;
import eu.toop.edm.regrep.SlotConsentToken;
import eu.toop.edm.regrep.SlotDataConsumer;
import eu.toop.edm.regrep.SlotDataSubjectLegalPerson;
import eu.toop.edm.regrep.SlotDataSubjectNaturalPerson;
import eu.toop.edm.regrep.SlotDatasetIdentifier;
import eu.toop.edm.regrep.SlotFullfillingRequirement;
import eu.toop.edm.regrep.SlotIssueDateTime;
import eu.toop.edm.regrep.SlotProcedure;
import eu.toop.regrep.RegRepHelper;
import eu.toop.regrep.query.QueryRequest;
import eu.toop.regrep.rim.InternationalStringValueType;
import eu.toop.regrep.rim.QueryType;

public class DataRequestCreator
{
  private static final ICommonsOrderedSet <String> TOP_LEVEL_SLOTS = new CommonsLinkedHashSet <> (SlotIssueDateTime.NAME,
                                                                                                  SlotProcedure.NAME,
                                                                                                  SlotFullfillingRequirement.NAME,
                                                                                                  SlotConsentToken.NAME,
                                                                                                  SlotDatasetIdentifier.NAME,
                                                                                                  SlotDataConsumer.NAME);

  private final EQueryDefinitionType m_eQueryDefinition;
  private final ICommonsOrderedMap <String, ISlotProvider> m_aProviders = new CommonsLinkedHashMap <> ();

  /**
   * Constructor
   *
   * @param eQueryDefinition
   *        Query definition type to be used. May not be <code>null</code>.
   * @param aProviders
   *        All slot providers to be added. May not be <code>null</code>.
   */
  private DataRequestCreator (@Nonnull final EQueryDefinitionType eQueryDefinition,
                              @Nonnull final ICommonsList <ISlotProvider> aProviders)
  {
    ValueEnforcer.notNull (eQueryDefinition, "QueryDefinition");
    ValueEnforcer.notEmptyNoNullValue (aProviders, "Providers");

    m_eQueryDefinition = eQueryDefinition;
    for (final ISlotProvider aItem : aProviders)
    {
      final String sName = aItem.getName ();
      if (m_aProviders.containsKey (sName))
        throw new IllegalArgumentException ("A slot provider for name '" + sName + "' is already present");
      m_aProviders.put (sName, aItem);
    }
  }

  @Nonnull
  QueryRequest createQueryRequest ()
  {
    final QueryRequest ret = RegRepHelper.createEmptyQueryRequest ();

    // All slots outside of query
    for (final String sTopLevel : TOP_LEVEL_SLOTS)
    {
      final ISlotProvider aSP = m_aProviders.get (sTopLevel);
      if (aSP != null)
        ret.addSlot (aSP.createSlot ());
    }

    final QueryType aQuery = new QueryType ();
    aQuery.setQueryDefinition (m_eQueryDefinition.getAttrValue ());

    // All slots inside of query
    for (final Map.Entry <String, ISlotProvider> aEntry : m_aProviders.entrySet ())
      if (!TOP_LEVEL_SLOTS.contains (aEntry.getKey ()))
        aQuery.addSlot (aEntry.getValue ().createSlot ());

    ret.setQuery (aQuery);

    return ret;
  }

  @Nonnull
  public static Builder builderCeoncept ()
  {
    return new Builder ().setQueryDefinition (EQueryDefinitionType.CONCEPT);
  }

  @Nonnull
  public static Builder builderDocument ()
  {
    return new Builder ().setQueryDefinition (EQueryDefinitionType.DOCUMENT);
  }

  public static class Builder
  {
    private EQueryDefinitionType m_eQueryDefinition;
    private LocalDateTime m_aIssueDateTime;
    private InternationalStringValueType m_aProcedure;
    private CCCEVRequirementType m_aFullfillingRequirement;
    private AgentType m_aDCAgent;
    private String m_sConsentToken;
    private String m_sDatasetIdentifier;
    private CoreBusinessType m_aDSLegalPerson;
    private CorePersonType m_aDSNaturalPerson;

    public Builder ()
    {}

    @Nonnull
    public Builder setQueryDefinition (@Nullable final EQueryDefinitionType eQueryDefinition)
    {
      m_eQueryDefinition = eQueryDefinition;
      return this;
    }

    @Nonnull
    public Builder setIssueDateTime (@Nullable final LocalDateTime aIssueDateTime)
    {
      m_aIssueDateTime = aIssueDateTime;
      return this;
    }

    @Nonnull
    public Builder setIssueDateTimeNow ()
    {
      return setIssueDateTime (PDTFactory.getCurrentLocalDateTime ());
    }

    @Nonnull
    public Builder setProcedure (@Nullable final InternationalStringValueType aProcedure)
    {
      m_aProcedure = aProcedure;
      return this;
    }

    @Nonnull
    public Builder setFullfillingRequirement (@Nullable final CCCEVRequirementType aFullfillingRequirement)
    {
      m_aFullfillingRequirement = aFullfillingRequirement;
      return this;
    }

    @Nonnull
    public Builder setConsentToken (@Nullable final String sConsentToken)
    {
      m_sConsentToken = sConsentToken;
      return this;
    }

    @Nonnull
    public Builder setDatasetIdentifier (@Nullable final String sDataSetIdentifier)
    {
      m_sDatasetIdentifier = sDataSetIdentifier;
      return this;
    }

    @Nonnull
    public Builder setDataConsumer (@Nullable final AgentType aAgent)
    {
      m_aDCAgent = aAgent;
      return this;
    }

    @Nonnull
    public Builder setDataSubject (@Nullable final CoreBusinessType aBusiness)
    {
      m_aDSLegalPerson = aBusiness;
      m_aDSNaturalPerson = null;
      return this;
    }

    @Nonnull
    public Builder setDataSubject (@Nullable final CorePersonType aPerson)
    {
      m_aDSLegalPerson = null;
      m_aDSNaturalPerson = aPerson;
      return this;
    }

    @Nonnull
    public QueryRequest build ()
    {
      if (m_eQueryDefinition == null)
        throw new IllegalStateException ("Query definition must be present");

      final ICommonsList <ISlotProvider> x = new CommonsArrayList <> ();
      if (m_aIssueDateTime != null)
        x.add (new SlotIssueDateTime (m_aIssueDateTime));
      if (m_aProcedure != null)
        x.add (new SlotProcedure (m_aProcedure));
      if (m_aFullfillingRequirement != null)
        x.add (new SlotFullfillingRequirement (m_aFullfillingRequirement));
      if (m_sConsentToken != null)
        x.add (new SlotConsentToken (m_sConsentToken));
      if (m_sDatasetIdentifier != null)
        x.add (new SlotDatasetIdentifier (m_sDatasetIdentifier));
      if (m_aDCAgent != null)
        x.add (new SlotDataConsumer (m_aDCAgent));
      if (m_aDSLegalPerson != null)
        x.add (new SlotDataSubjectLegalPerson (m_aDSLegalPerson));
      else
        if (m_aDSNaturalPerson != null)
          x.add (new SlotDataSubjectNaturalPerson (m_aDSNaturalPerson));
      return new DataRequestCreator (m_eQueryDefinition, x).createQueryRequest ();
    }
  }
}
